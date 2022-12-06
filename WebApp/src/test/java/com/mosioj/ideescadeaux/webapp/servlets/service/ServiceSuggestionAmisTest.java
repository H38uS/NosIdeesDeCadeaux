package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        User seven = UsersRepository.getUser(7).orElseThrow(SQLException::new);
        User eight = UsersRepository.getUser(8).orElseThrow(SQLException::new);
        UserRelationsRepository.deleteAssociation(firefox, seven);
        UserRelationsRepository.deleteAssociation(firefox, eight);
        UserRelationsSuggestionRepository.removeIfExists(_OWNER_ID_, 7);
        UserRelationsSuggestionRepository.removeIfExists(_OWNER_ID_, 8);
        UserRelationRequestsRepository.cancelRequest(firefox, seven);
        UserRelationRequestsRepository.cancelRequest(firefox, eight);
        UserRelationsSuggestionRepository.newSuggestion(theAdmin,
                                                        firefox,
                                                        UsersRepository.getUser(7).orElseThrow(SQLException::new));
        UserRelationsSuggestionRepository.newSuggestion(theAdmin,
                                                        firefox,
                                                        UsersRepository.getUser(8).orElseThrow(SQLException::new));

        // When processing two suggestions resolution (one accepted one rejected)
        final String selectedOne = "selected_" + 7;
        final String rejectedOne = "rejected_" + 8;
        bindRequestParamMap(Map.of(selectedOne,
                                   new String[]{selectedOne, "true"},
                                   rejectedOne,
                                   new String[]{rejectedOne, "true"}));
        ServiceResponse<?> resp = doTestServicePost(ServiceResponse.class);

        // Then I have sent a new friendship request and only one new :'(
        assertTrue(resp.isOK());
        // a new friend with 7!
        assertTrue(UserRelationRequestsRepository.associationExists(firefox, seven));
        assertFalse(UserRelationsSuggestionRepository.hasReceivedSuggestionOf(_MOI_AUTRE_, 7));
        // but not with 8...
        assertFalse(UserRelationRequestsRepository.associationExists(firefox, eight));
        assertFalse(UserRelationsSuggestionRepository.hasReceivedSuggestionOf(_MOI_AUTRE_, 8));
    }

}