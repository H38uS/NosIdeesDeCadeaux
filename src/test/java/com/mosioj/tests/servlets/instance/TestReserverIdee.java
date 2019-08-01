package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

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
	public void test() throws SQLException, NoRowsException {

		int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, null, null);
		Idee idee = idees.getIdeaWithoutEnrichment(id);

		int recurentUnbook = notif.addNotification(_OWNER_ID_, new NotifRecurentIdeaUnbook(friendOfFirefox, idee));
		assertNotifDoesExists(recurentUnbook);

		when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);
		idee = idees.getIdeaWithoutEnrichment(id);

		assertNotifDoesNotExists(recurentUnbook);
		assertTrue(idee.isBooked());
	}

	@Test
	public void testReservationSurprise() throws SQLException {
		
		int id = idees.addIdea(friendOfFirefox, "reservation", "", 0, null, firefox, firefox);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		
		when(request.getParameter(ReserverIdee.IDEA_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);
		idee = idees.getIdeaWithoutEnrichment(id);
		
		assertTrue(idee.isBooked());
	}

}
