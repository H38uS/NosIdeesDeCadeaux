package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceGetUserNameFromIDTest extends AbstractTestServletWebApp {

    public ServiceGetUserNameFromIDTest() {
        super(new ServiceGetUserNameFromID());
    }

    @Test
    public void testSuccess() {

        bindGetRequestParam(ServiceGetUserNameFromID.USER_ID_PARAM, friendOfFirefox.id + "");

        ThisServiceResp resp = doTestServiceGet(ThisServiceResp.class);

        assertTrue(resp.isOK());
        assertEquals(friendOfFirefox, resp.getMessage());
        assertEquals("test@toto.com", resp.getMessage().getEmail());
    }

    private static class ThisServiceResp extends ServiceResponse<User> {
        // For json conversion

        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         */
        public ThisServiceResp(boolean isOK, User message, User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}