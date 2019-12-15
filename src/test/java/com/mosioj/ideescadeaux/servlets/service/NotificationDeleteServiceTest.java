package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.model.repositories.Notifications.TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class NotificationDeleteServiceTest extends AbstractTestServlet {

    public NotificationDeleteServiceTest() {
        super(new NotificationDeleteService());
    }

    @Test
    public void testDeleteSuccess() throws SQLException {

        AbstractNotification n = notif.getUserNotifications(firefox.id).get(0);
        when(request.getParameter(NotificationDeleteService.NOTIFICATION_PARAMETER)).thenReturn(n.id + "");

        ServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select 1 from " + TABLE_NAME + " where id = ?", n.id));
    }
}