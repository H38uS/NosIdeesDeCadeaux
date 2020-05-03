package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceGetUserNameFromIDTestWebApp extends AbstractTestServletWebApp {

    public ServiceGetUserNameFromIDTestWebApp() {
        super(new ServiceGetUserNameFromID());
    }

    @Test
    public void testSuccess() {

        when(request.getParameter(ServiceGetUserNameFromID.USER_ID_PARAM)).thenReturn(friendOfFirefox.id + "");

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
         * @param isAdmin Whether the user is an admin.
         */
        public ThisServiceResp(boolean isOK, User message, boolean isAdmin) {
            super(isOK, message, isAdmin);
        }
    }
}