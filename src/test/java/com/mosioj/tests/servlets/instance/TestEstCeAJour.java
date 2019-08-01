package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.servlets.controllers.idees.reservation.ReserverIdee;
import com.mosioj.servlets.service.ServiceEstAJour;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.NoRowsException;

public class TestEstCeAJour extends AbstractTestServlet {

	public TestEstCeAJour() {
		super(new ServiceEstAJour());
	}

	@Test
	public void test() throws SQLException, NoRowsException {

		int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);

		notif.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
		List<AbstractNotification> notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
		assertEquals(0, notifs.size());

		when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
		assertEquals(1, notifs.size());
	}
	
	@Test
	public void testSurprise() throws SQLException, NoRowsException {

		int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);

		notif.removeAllType(friendOfFirefox, NotificationType.IS_IDEA_UP_TO_DATE);
		List<AbstractNotification> notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
		assertEquals(0, notifs.size());

		when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		notifs = notif.getUserNotifications(friendOfFirefox.id, NotificationType.IS_IDEA_UP_TO_DATE);
		assertEquals(0, notifs.size()); // On ne peut pas demander sur une surprise
	}

}
