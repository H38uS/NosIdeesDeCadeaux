package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceReserverTestWebApp extends AbstractTestServletWebApp {

    public ServiceReserverTestWebApp() {
        super(new ServiceReserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }

    @Test
    public void testReservationSurprise() throws SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertFalse(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));

        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }

    @Test
    public void bookingShouldRemoveUnbookNotification() throws SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);

        int recurentUnbook = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                     new NotifRecurentIdeaUnbook(friendOfFirefox,
                                                                                                 idee));
        assertNotifDoesExists(recurentUnbook);

        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertNotifDoesNotExists(recurentUnbook);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }
}