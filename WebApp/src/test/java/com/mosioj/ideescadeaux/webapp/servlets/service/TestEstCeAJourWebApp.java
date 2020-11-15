package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestEstCeAJourWebApp extends AbstractTestServletWebApp {

    public TestEstCeAJourWebApp() {
        super(new ServiceEstAJour());
    }

    @Test
    public void test() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);


        NotificationsRepository.terminator()
                               .whereOwner(friendOfFirefox)
                               .whereType(NType.IS_IDEA_UP_TO_DATE)
                               .terminates();
        List<Notification> notifs = NotificationsRepository.getUserNotifications(friendOfFirefox,
                                                                                 NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(1, notifs.size());
    }

    @Test
    public void testSurprise() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);

        NotificationsRepository.terminator()
                               .whereOwner(friendOfFirefox)
                               .whereType(NType.IS_IDEA_UP_TO_DATE)
                               .terminates();
        List<Notification> notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost(); // bloqué par la police, impossible en utilisation classique...

        assertFalse(resp.isOK());
        assertEquals("Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !",
                     resp.getMessage());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox, NType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size()); // On ne peut pas demander sur une surprise
    }

    @Test
    public void testTriggeringItTwiceIsNotAllowed() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());
    }
}
