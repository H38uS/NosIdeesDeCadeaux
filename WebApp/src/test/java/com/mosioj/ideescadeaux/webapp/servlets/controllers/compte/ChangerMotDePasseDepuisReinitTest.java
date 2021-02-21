package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.repositories.UserChangePwdRequestRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ChangerMotDePasseDepuisReinitTest extends AbstractTestServletWebApp {

    public ChangerMotDePasseDepuisReinitTest() {
        super(new ChangerMotDePasseDepuisReinit());
    }

    @Test
    public void userCanUpdateTheirPwdWithAToken() throws SQLException {

        // Delete previous request
        UserChangePwdRequestRepository.deleteAssociation(friendOfFirefox.id);

        // Create the request
        int token = new Random().nextInt();
        UserChangePwdRequestRepository.createNewRequest(friendOfFirefox.id, token);
        assertTrue(UserChangePwdRequestRepository.isAValidCombinaison(friendOfFirefox.id, token));

        // Do the request
        when(request.getRequestDispatcher(ChangerMotDePasseDepuisReinit.SUCCES_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter("userIdParam")).thenReturn(String.valueOf(friendOfFirefox.id));
        when(request.getParameter("tokenId")).thenReturn(String.valueOf(token));
        when(request.getParameter("pwd1")).thenReturn("thenewmdp12");
        when(request.getParameter("pwd2")).thenReturn("thenewmdp12");
        doTestPost();

        // Verify
        assertFalse(UserChangePwdRequestRepository.isAValidCombinaison(friendOfFirefox.id, token));
    }
}