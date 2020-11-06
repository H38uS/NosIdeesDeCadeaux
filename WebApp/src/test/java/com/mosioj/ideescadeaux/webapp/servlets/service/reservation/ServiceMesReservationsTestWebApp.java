package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceMesReservationsTestWebApp extends AbstractTestServletWebApp {

    public ServiceMesReservationsTestWebApp() {
        super(new ServiceMesReservations());
    }

    @Test
    public void testSuccess() {

        MyServiceResp resp = doTestServiceGet(MyServiceResp.class);

        assertTrue(resp.isOK());
        assertTrue(resp.getMessage().getTheContent().size() > 0);

        OwnerIdeas first = resp.getMessage().getTheContent().get(0);
        assertEquals("test@toto.com", first.getOwner().email);
        assertEquals("Test@toto.com", first.getOwner().name);
        assertEquals(4, first.getOwner().id);
        assertEquals("<p>toto</p>\n<p>tutu</p>", first.getIdeas().get(0).getIdee().getHtml().trim());
    }

    private static class MyServiceResp extends ServiceResponse<PagedResponse<List<OwnerIdeas>>> {
        // for json

        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         * @param isAdmin Whether the user is an admin.
         */
        public MyServiceResp(boolean isOK,
                             PagedResponse<List<OwnerIdeas>> message,
                             boolean isAdmin,
                             User connectedUser) {
            super(isOK, message, isAdmin, connectedUser);
        }
    }
}