package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceSuggestionRejoindreReseauTest extends AbstractTestServletWebApp {

    public ServiceSuggestionRejoindreReseauTest() {
        super(new ServiceSuggestionRejoindreReseau());
    }

    @Test
    public void testEnvoieSuggestion() {

        // Given Djoeee (22) is not a friend of admin, and has not received any suggestion
        UserRelationsRepository.deleteAssociation(theAdmin, jo3);
        UserRelationsSuggestionRepository.removeIfExists(theAdmin.id, jo3.id);

        // When posting a suggestion request
        when(request.getParameter(ServiceSuggestFriendToOther.USER_PARAMETER)).thenReturn(String.valueOf(theAdmin.id));
        Map<String, String[]> params = new HashMap<>();
        final String selectedOne = "selected_" + jo3.id;
        params.put(selectedOne, new String[]{selectedOne, "true"});
        when(request.getParameterMap()).thenReturn(params);
        StringServiceResponse resp = doTestServicePost();

        // Then the request is successfully created
        assertTrue(resp.isOK());
        assertEquals("Les utilisateurs suivants ont bien été ajouté: [Djoe&icirc;&eacute;&egrave;&ocirc;e (jo3@hotmadzzdzil.frtrr)]", resp.getMessage());
        assertTrue(UserRelationsSuggestionRepository.hasReceivedSuggestionOf(theAdmin.id, jo3.id));
    }
}