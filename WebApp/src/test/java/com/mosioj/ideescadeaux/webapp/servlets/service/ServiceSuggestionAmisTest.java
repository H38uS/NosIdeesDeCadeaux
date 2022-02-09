package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceSuggestionAmisTest extends AbstractTestServletWebApp {

    public ServiceSuggestionAmisTest() {
        super(new ServiceSuggestionAmis());
    }

    @Test
    public void listSuggestionAmis() {
        // Should not throw
        UserRelationsSuggestionRepository.getUserSuggestions(firefox);
    }

    @Test
    public void testResolveSuggestion() throws SQLException {

        // Given two persons that are not yet our friends
        // for whom we have received a suggestion to ask them for friendship
        UserRelationsRepository.deleteAssociation(_OWNER_ID_, 7);
        UserRelationsRepository.deleteAssociation(_OWNER_ID_, 8);
        UserRelationsSuggestionRepository.removeIfExists(_OWNER_ID_, 7);
        UserRelationsSuggestionRepository.removeIfExists(_OWNER_ID_, 8);
        UserRelationRequestsRepository.cancelRequest(_OWNER_ID_, 7);
        UserRelationRequestsRepository.cancelRequest(_OWNER_ID_, 8);
        UserRelationsSuggestionRepository.newSuggestion(theAdmin,
                                                        firefox,
                                                        UsersRepository.getUser(7).orElseThrow(SQLException::new));
        UserRelationsSuggestionRepository.newSuggestion(theAdmin,
                                                        firefox,
                                                        UsersRepository.getUser(8).orElseThrow(SQLException::new));

        // When processing two suggestions resolution (one accepted one rejected)
        Map<String, String[]> params = new HashMap<>();
        final String selectedOne = "selected_" + 7;
        final String rejectedOne = "rejected_" + 8;
        params.put(selectedOne, new String[]{selectedOne, "true"});
        params.put(rejectedOne, new String[]{rejectedOne, "false"});
        when(request.getParameterMap()).thenReturn(params);
        ServiceResponse<?> resp = doTestServicePost(ServiceResponse.class);

        // Then I have sent a new friendship request and only one new :'(
        assertTrue(resp.isOK());
        // a new friend with 7!
        assertTrue(UserRelationRequestsRepository.associationExists(_OWNER_ID_, 7));
        assertFalse(UserRelationsSuggestionRepository.hasReceivedSuggestionOf(_OWNER_ID_, 7));
        // but not with 8...
        assertFalse(UserRelationRequestsRepository.associationExists(_OWNER_ID_, 8));
        assertFalse(UserRelationsSuggestionRepository.hasReceivedSuggestionOf(_OWNER_ID_, 8));
    }

}