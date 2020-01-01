package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TestDemandeRejoindreReseau extends AbstractTestServlet {

    public TestDemandeRejoindreReseau() {
        super(new DemandeRejoindreReseauService());
    }

    @Before
    public void before() {
        when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
    }

    @Test
    public void testPostEmptyParameters() {

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost(false);

        // Test parameters call
        verify(request).getParameter(eq("user_id"));
        verify(request, atMost(1)).getParameter(anyString());
        assertNull(resp); // On a pas passé la sécurité
    }

    @Test
    public void testPostSuccess() throws SQLException {

        final int otherUserNotFriendYet = 23;
        UserRelationRequestsRepository.cancelRequest(_OWNER_ID_, otherUserNotFriendYet);

        int suggestionAndAsk = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                       new NotifNewRelationSuggestion(otherUserNotFriendYet,
                                                                                            "Toto"));
        int suggestionAndAsked = NotificationsRepository.addNotification(otherUserNotFriendYet,
                                                                         new NotifNewRelationSuggestion(_OWNER_ID_, "Toto"));
        assertNotifDoesExists(suggestionAndAsk);
        assertNotifDoesExists(suggestionAndAsked);

        // Should not throw an exception
        when(request.getParameter("user_id")).thenReturn("23");
        StringServiceResponse resp = doTestServicePost();

        assertNotifDoesNotExists(suggestionAndAsk);
        assertNotifDoesNotExists(suggestionAndAsked);
        assertTrue(resp.isOK());
    }

    @Test
    public void testAlreadySent() {

        when(request.getParameter("user_id")).thenReturn("10");

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Vous avez déjà envoyé une demande à The Toto.", resp.getMessage());
    }

    @Test
    public void testGroupAlreadyExist() {

        when(request.getParameter("user_id")).thenReturn("1");

        // Should not throw an exception
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Vous faites déjà parti du réseau de Jordan.mosio@hotmail.fr.", resp.getMessage());
    }

}
