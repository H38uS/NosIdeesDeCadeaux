package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestEstCeAJourWebApp extends AbstractTestServletWebApp {

    public TestEstCeAJourWebApp() {
        super(new ServiceEstAJour());
    }

    @Test
    public void test() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("reservation")
                                                    .withPriority(p));

        NotificationsRepository.terminator()
                               .whereOwner(friendOfFirefox)
                               .whereType(NType.IS_IDEA_UP_TO_DATE)
                               .terminates();
        var notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        bindPostRequestParam(ServiceEstAJour.IDEE_FIELD_PARAMETER, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(1, notifs.size());
    }

    @Test
    public void testSurprise() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("reservation")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));

        NotificationsRepository.terminator()
                               .whereOwner(friendOfFirefox)
                               .whereType(NType.IS_IDEA_UP_TO_DATE)
                               .terminates();
        var notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        bindPostRequestParam(ServiceEstAJour.IDEE_FIELD_PARAMETER, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost(); // bloqué par la police, impossible en utilisation classique...

        assertFalse(resp.isOK());
        assertEquals("Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !",
                     resp.getMessage());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size()); // On ne peut pas demander sur une surprise
    }

    @Test
    public void testTriggeringItTwiceIsNotAllowed() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("reservation")
                                                    .withPriority(p));

        bindPostRequestParam(ServiceEstAJour.IDEE_FIELD_PARAMETER, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());
    }
}
