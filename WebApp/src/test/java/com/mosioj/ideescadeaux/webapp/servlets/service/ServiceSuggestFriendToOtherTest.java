package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.PossibleSuggestion;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceSuggestFriendToOtherTest extends AbstractTestServletWebApp {

    public ServiceSuggestFriendToOtherTest() {
        super(new ServiceSuggestFriendToOther());
    }

    @Test
    public void testGetPossibleSuggestions() throws SQLException {

        // Given a friend to suggest some others
        when(request.getParameter(ServiceSuggestFriendToOther.USER_PARAMETER)).thenReturn(String.valueOf(theAdmin.id));

        // When doing the get
        PossibleSuggestionResponse resp = doTestServiceGet(PossibleSuggestionResponse.class);

        // Then the response is OK and contains the expected suggestions
        assertTrue(resp.isOK());
        List<PossibleSuggestion> suggestions = resp.getMessage();
        assertEquals(Arrays.asList(new PossibleSuggestion(UsersRepository.getUser(6).orElseThrow(SQLException::new)),
                                   new PossibleSuggestion(jo3).withReason(
                                           "Jordan.mosio@hotmail.fr a déjà reçu une suggestion pour Djoe&icirc;&eacute;&egrave;&ocirc;e."),
                                   new PossibleSuggestion(UsersRepository.getUser(13)
                                                                         .orElseThrow(SQLException::new)).withReason(
                                           "Jordan.mosio@hotmail.fr a déjà envoyé une demande à Iihi."),
                                   new PossibleSuggestion(friendOfFirefox).withReason(
                                           "Test@toto.com a déjà envoyé une demande à Jordan.mosio@hotmail.fr.")),
                     suggestions);
    }

    private static class PossibleSuggestionResponse extends ServiceResponse<List<PossibleSuggestion>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param isAdmin       Whether the user is an admin.
         * @param connectedUser The connected user or null if none.
         */
        public PossibleSuggestionResponse(boolean isOK,
                                          List<PossibleSuggestion> message,
                                          boolean isAdmin,
                                          User connectedUser) {
            super(isOK, message, isAdmin, connectedUser);
        }
    }
}