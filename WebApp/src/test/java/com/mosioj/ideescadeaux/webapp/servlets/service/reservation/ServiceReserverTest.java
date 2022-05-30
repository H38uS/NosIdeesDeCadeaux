package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.RECURENT_IDEA_UNBOOK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceReserverTest extends AbstractTestServletWebApp {

    public ServiceReserverTest() {
        super(new ServiceReserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }

    @Test
    public void testReservationSurprise() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);
        assertFalse(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));

        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }

    @Test
    public void bookingShouldRemoveUnbookNotification() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

        int recurentUnbook = RECURENT_IDEA_UNBOOK.with(friendOfFirefox, idee).sendItTo(firefox);
        assertNotifDoesExists(recurentUnbook);

        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertNotifDoesNotExists(recurentUnbook);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }
}