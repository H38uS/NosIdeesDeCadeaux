package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.repositories.UserChangePwdRequestRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        bindRequestParam("userIdParam", friendOfFirefox.id);
        bindRequestParam("tokenId", token);
        bindRequestParam("pwd1", "thenewmdp12");
        bindRequestParam("pwd2", "thenewmdp12");
        doTestPost();

        // Verify
        assertFalse(UserChangePwdRequestRepository.isAValidCombinaison(friendOfFirefox.id, token));
    }
}