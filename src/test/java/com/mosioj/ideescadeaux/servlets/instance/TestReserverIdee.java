package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.idees.reservation.ReserverIdee;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestReserverIdee extends AbstractTestServlet {

    public TestReserverIdee() {
        super(new ReserverIdee());
    }

    @Test
    public void test() throws SQLException {

        int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);
        Idee idee = idees.getIdeaWithoutEnrichment(id);

        int recurentUnbook = notif.addNotification(_OWNER_ID_, new NotifRecurentIdeaUnbook(friendOfFirefox, idee));
        assertNotifDoesExists(recurentUnbook);

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestPost();
        idee = idees.getIdeaWithoutEnrichment(id);

        assertNotifDoesNotExists(recurentUnbook);
        assertTrue(idee.isBooked());
    }

    @Test
    public void testReservationSurprise() throws SQLException {

        int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);
        Idee idee = idees.getIdeaWithoutEnrichment(id);
        assertFalse(idee.isBooked());

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestPost();
        idee = idees.getIdeaWithoutEnrichment(id);

        assertTrue(idee.isBooked());
    }

}
