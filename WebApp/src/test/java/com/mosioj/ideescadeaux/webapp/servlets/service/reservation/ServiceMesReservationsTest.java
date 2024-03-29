package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.EntityWithText;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ServiceMesReservationsTest extends AbstractTestServletWebApp {

    public ServiceMesReservationsTest() {
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
        assertEquals(Optional.of("<p>reservation</p>"), first.getIdeas().stream().map(DecoratedWebAppIdea::getIdee)
                                                             .map(EntityWithText::getHtml)
                                                             .map(String::trim)
                                                             .filter("<p>reservation</p>"::equals)
                                                             .findFirst());
    }

    @Test
    public void deletedIdeasAreNotInMyBooking() throws SQLException {

        // Deleting a booked idea
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("Deleted IDea")
                                                    .withPriority(p)
                                                    .withCreatedBy(friendOfFirefox));
        IdeesRepository.reserver(idee, firefox);
        IdeesRepository.remove(idee);
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
        assertTrue(IdeesRepository.getDeletedIdea(idee.getId()).isPresent());

        // Fetching the booking
        MyServiceResp resp = doTestServiceGet(MyServiceResp.class);
        assertTrue(resp.isOK());
        List<DecoratedWebAppIdea> friendOfFirefoxIdeas = resp.getMessage()
                                                             .getTheContent()
                                                             .stream()
                                                             .filter(oi -> friendOfFirefox.equals(oi.getOwner()))
                                                             .map(OwnerIdeas::getIdeas)
                                                             .findFirst()
                                                             .orElseThrow(SQLException::new);

        // Checking that the deleted ones is not there
        assertFalse(friendOfFirefoxIdeas.isEmpty());
        assertFalse(friendOfFirefoxIdeas.stream().map(DecoratedWebAppIdea::getIdee).anyMatch(idee::equals));
    }

    private static class MyServiceResp extends ServiceResponse<PagedResponse<List<OwnerIdeas>>> {
        // for json

        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         */
        public MyServiceResp(boolean isOK,
                             PagedResponse<List<OwnerIdeas>> message,
                             User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}