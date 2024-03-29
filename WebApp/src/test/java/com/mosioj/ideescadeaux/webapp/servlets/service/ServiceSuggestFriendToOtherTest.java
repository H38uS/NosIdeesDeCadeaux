package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.PossibleSuggestion;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceSuggestFriendToOtherTest extends AbstractTestServletWebApp {

    public ServiceSuggestFriendToOtherTest() {
        super(new ServiceSuggestFriendToOther());
    }

    @Test
    public void testGetPossibleSuggestions() throws SQLException {

        // Given a friend to suggest some others
        bindGetRequestParam(ServiceSuggestFriendToOther.USER_PARAMETER, String.valueOf(theAdmin.id));

        // When doing the get
        PossibleSuggestionResponse resp = doTestServiceGet(PossibleSuggestionResponse.class);

        // Then the response is OK and contains the expected suggestions
        assertTrue(resp.isOK());
        List<PossibleSuggestion> suggestions = resp.getMessage();
        assertEquals(List.of(new PossibleSuggestion(friendOfFirefox).withReason(
                                     "Test@toto.com a déjà envoyé une demande à Jordan.mosio@hotmail.fr."),
                             new PossibleSuggestion(UsersRepository.getUser(6).orElseThrow(SQLException::new)),
                             new PossibleSuggestion(jo3).withReason(
                                     "Jordan.mosio@hotmail.fr a déjà reçu une suggestion pour Djoe&icirc;&eacute;&egrave;&ocirc;e."),
                             new PossibleSuggestion(UsersRepository.getUser(13)
                                                                   .orElseThrow(SQLException::new)).withReason(
                                     "Jordan.mosio@hotmail.fr a déjà envoyé une demande à Iihi.")
                     ),
                     suggestions);
    }

    @Test
    public void testSpecialCharacter() {
        // Given a friend to suggest some others and a name with special character
        bindGetRequestParam(ServiceSuggestFriendToOther.USER_PARAMETER, String.valueOf(theAdmin.id));
        bindGetRequestParam("name", "Djoeîéèôe ");

        // When doing the get
        PossibleSuggestionResponse resp = doTestServiceGet(PossibleSuggestionResponse.class);

        // Then the response is OK and contains the expected suggestions
        assertTrue(resp.isOK());
        assertEquals(List.of(new PossibleSuggestion(jo3).withReason(
                             "Jordan.mosio@hotmail.fr a déjà reçu une suggestion pour Djoe&icirc;&eacute;&egrave;&ocirc;e.")),
                     resp.getMessage());
    }

    private static class PossibleSuggestionResponse extends ServiceResponse<List<PossibleSuggestion>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param connectedUser The connected user or null if none.
         */
        public PossibleSuggestionResponse(boolean isOK,
                                          List<PossibleSuggestion> message,
                                          User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}