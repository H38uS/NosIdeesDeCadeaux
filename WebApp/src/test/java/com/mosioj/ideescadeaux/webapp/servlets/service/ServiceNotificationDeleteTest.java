package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceNotificationDeleteTest extends AbstractTestServletWebApp {

    public ServiceNotificationDeleteTest() {
        super(new ServiceNotificationDelete());
    }

    @Test
    public void testDeleteSuccess() {

        Notification n = NotificationsRepository.fetcher().whereOwner(firefox).fetch().get(0);
        bindRequestParam(ServiceNotificationDelete.NOTIFICATION_PARAMETER, String.valueOf(n.id));

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select 1 from NOTIFICATIONS where id = ?", n.id));
    }
}