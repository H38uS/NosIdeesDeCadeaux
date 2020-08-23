package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
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

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

        NotificationsRepository.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
        List<AbstractNotification> notifs = NotificationsRepository.getUserNotifications(friendOfFirefox.id,
                                                                                         NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(1, notifs.size());
    }

    @Test
    public void testSurprise() throws SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);

        NotificationsRepository.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
        List<AbstractNotification> notifs = NotificationsRepository.getUserNotifications(friendOfFirefox.id,
                                                                                         NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost(); // bloqué par la police, impossible en utilisation classique...

        assertFalse(resp.isOK());
        assertEquals("Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !",
                     resp.getMessage());
        notifs = NotificationsRepository.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size()); // On ne peut pas demander sur une surprise
    }

    @Test
    public void testTriggeringItTwiceIsNotAllowed() throws SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

        when(request.getParameter(ServiceEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());
    }
}
