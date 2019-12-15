package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServiceMesReservationsTest extends AbstractTestServlet {

    public ServiceMesReservationsTest() {
        super(new ServiceMesReservations());
    }

    @Test
    public void testSuccess() {

        MyServiceResp resp = doTestServiceGet(MyServiceResp.class);

        assertTrue(resp.isOK());
        assertTrue(resp.getMessage().size() > 0);

        OwnerIdeas first = resp.getMessage().get(0);
        assertEquals("test@toto.com", first.getOwner().email);
        assertEquals("Test@toto.com", first.getOwner().name);
        assertEquals(4, first.getOwner().id);
        assertEquals("toto\r\n\r\ntutu", first.getIdeas().get(0).getText());
    }

    private static class MyServiceResp extends ServiceResponse<List<OwnerIdeas>> {
        // for json
        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         * @param isAdmin Whether the user is an admin.
         */
        public MyServiceResp(boolean isOK, List<OwnerIdeas> message, boolean isAdmin) {
            super(isOK, message, isAdmin);
        }
    }
}