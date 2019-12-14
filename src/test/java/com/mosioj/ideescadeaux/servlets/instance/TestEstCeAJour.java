package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.idees.reservation.ReserverIdee;
import com.mosioj.ideescadeaux.servlets.service.ServiceEstAJour;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestEstCeAJour extends AbstractTestServlet {

    public TestEstCeAJour() {
        super(new ServiceEstAJour());
    }

    @Test
    public void test() throws SQLException, ServletException, IOException {

        int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

        notif.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
        List<AbstractNotification> notifs = notif.getUserNotifications(friendOfFirefox.id,
                                                                       NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        ServiceResponse resp = doTestServicePost(request, response);
        doTestPost(request, response);

        assertTrue(resp.isOK());
        notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(1, notifs.size());
    }

    @Test
    public void testSurprise() throws SQLException, ServletException, IOException {

        int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);

        notif.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
        List<AbstractNotification> notifs = notif.getUserNotifications(friendOfFirefox.id,
                                                                       NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size());

        when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestServicePost(request, response); // bloqué par la police, impossible en utilisation classique...

        notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
        assertEquals(0, notifs.size()); // On ne peut pas demander sur une surprise
    }

    // FIXME : 0 faire un test sur le fail : quand on demande deux fois de suite ?
}
