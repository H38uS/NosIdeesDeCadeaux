package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository.TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceNotificationDeleteTest extends AbstractTestServletWebApp {

    public ServiceNotificationDeleteTest() {
        super(new ServiceNotificationDelete());
    }

    @Test
    public void testDeleteSuccess() {

        Notification n = NotificationsRepository.fetcher().whereOwner(firefox).fetch().get(0);
        when(request.getParameter(ServiceNotificationDelete.NOTIFICATION_PARAMETER)).thenReturn(String.valueOf(n.id));

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select 1 from " + TABLE_NAME + " where id = ?", n.id));
    }
}