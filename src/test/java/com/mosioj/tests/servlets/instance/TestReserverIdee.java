package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.servlets.controllers.idees.reservation.ReserverIdee;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.NoRowsException;

public class TestReserverIdee extends AbstractTestServlet {

	public TestReserverIdee() {
		super(new ReserverIdee());
	}

	@Test
	public void test() throws SQLException, NoRowsException, ServletException, IOException {

		int id = idees.addIdea(_FRIEND_ID_, "reservation", "", 0, null, null);
		Idee idee = idees.getIdea(id);

		int recurentUnbook = notif.addNotification(_OWNER_ID_, new NotifRecurentIdeaUnbook(friendOfFirefox, idee));
		assertNotifDoesExists(recurentUnbook);

		when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
		doTestGet(request, response); // FIXME 0 : WTF, go post !
		idee = idees.getIdea(id);

		assertNotifDoesNotExists(recurentUnbook);
		assertTrue(idee.isBooked());
	}

}
