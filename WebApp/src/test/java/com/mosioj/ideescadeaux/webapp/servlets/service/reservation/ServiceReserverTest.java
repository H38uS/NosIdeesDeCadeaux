package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.RECURENT_IDEA_UNBOOK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServiceReserverTest extends AbstractTestServletWebApp {

    public ServiceReserverTest() {
        super(new ServiceReserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).get(0);
        bindRequestParam(ServiceDereserver.IDEA_ID_PARAM, idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }

    @Test
    public void testReservationSurprise() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("reservation")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));
        assertFalse(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));

        bindRequestParam(ServiceDereserver.IDEA_ID_PARAM, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }

    @Test
    public void bookingShouldRemoveUnbookNotification() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("reservation")
                                                    .withPriority(p));

        int recurentUnbook = RECURENT_IDEA_UNBOOK.with(friendOfFirefox, idee).sendItTo(firefox);
        assertNotifDoesExists(recurentUnbook);

        bindRequestParam(ServiceDereserver.IDEA_ID_PARAM, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertNotifDoesNotExists(recurentUnbook);
        assertTrue(idee.getBookingInformation().map(BookingInformation::isBooked).orElseThrow(SQLException::new));
    }
}