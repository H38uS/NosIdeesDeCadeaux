package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.database.NoRowsException;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.ReserverIdee;
import org.junit.Assert;
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
    public void test() throws NoRowsException, SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id);

        int recurentUnbook = NotificationsRepository.addNotification(_OWNER_ID_, new NotifRecurentIdeaUnbook(friendOfFirefox, idee));
        assertNotifDoesExists(recurentUnbook);

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestPost();
        idee = IdeesRepository.getIdeaWithoutEnrichment(id);

        assertNotifDoesNotExists(recurentUnbook);
        assertTrue(idee.isBooked());
    }

    @Test
    public void testReservationSurprise() throws NoRowsException, SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);
        Idee idee = null;
        try {
            idee = IdeesRepository.getIdeaWithoutEnrichment(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertFalse(idee.isBooked());

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestPost();
        try {
            idee = IdeesRepository.getIdeaWithoutEnrichment(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail();
        }

        assertTrue(idee.isBooked());
    }

}
