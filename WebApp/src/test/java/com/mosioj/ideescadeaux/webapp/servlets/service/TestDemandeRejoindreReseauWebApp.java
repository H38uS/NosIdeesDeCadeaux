package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_RELATION_SUGGESTION;
import static org.junit.Assert.*;

public class TestDemandeRejoindreReseauWebApp extends AbstractTestServletWebApp {

    public TestDemandeRejoindreReseauWebApp() {
        super(new ServiceDemandeRejoindreReseau());
    }

    @Test
    public void testPostEmptyParameters() {

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost();

        // Test parameters call
        assertFalse(resp.isOK());
        assertEquals("Aucun utilisateur trouvé en paramètre.", resp.getMessage());
    }

    @Test
    public void testPostSuccess() throws SQLException {

        final User otherNotFriend = UsersRepository.getUser(23).orElseThrow(SQLException::new);
        UserRelationRequestsRepository.cancelRequest(firefox, otherNotFriend);

        int suggestionAndAsk = NEW_RELATION_SUGGESTION.with(otherNotFriend).sendItTo(firefox);
        int suggestionAndAsked = NEW_RELATION_SUGGESTION.with(firefox).sendItTo(otherNotFriend);
        assertNotifDoesExists(suggestionAndAsk);
        assertNotifDoesExists(suggestionAndAsked);

        // Should not throw an exception
        bindRequestParam("user_id", "23");
        StringServiceResponse resp = doTestServicePost();

        assertNotifDoesNotExists(suggestionAndAsk);
        assertNotifDoesNotExists(suggestionAndAsked);
        assertTrue(resp.isOK());
    }

    @Test
    public void testAlreadySent() {

        bindRequestParam("user_id", "10");

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Vous avez déjà envoyé une demande à The Toto.", resp.getMessage());
    }

    @Test
    public void testGroupAlreadyExist() {

        bindRequestParam("user_id", "1");

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Vous faites déjà parti du réseau de Jordan.mosio@hotmail.fr.", resp.getMessage());
    }

}
