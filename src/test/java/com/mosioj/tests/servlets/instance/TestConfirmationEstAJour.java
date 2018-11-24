package com.mosioj.tests.servlets.instance;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.servlets.controllers.compte.MesNotifications;
import com.mosioj.servlets.controllers.idees.ConfirmationEstAJour;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.NoRowsException;

public class TestConfirmationEstAJour extends AbstractTestServlet {

	public TestConfirmationEstAJour() {
		super(new ConfirmationEstAJour());
	}

	@Test
	public void testAskAndAnswerYes() throws SQLException, NoRowsException, ServletException, IOException {

		int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
		Idee idee = idees.getIdeaWithoutEnrichment(id);

		int notifId = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
		assertNotifDoesExists(notifId);

		when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
		when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id + "");
		doTestGet(request, response);
		// doTestPost(request, response);

		assertNotifDoesNotExists(notifId);
	}

	@Test
	public void testOnANewIdea() throws SQLException, NoRowsException, ServletException, IOException {

		when(session.getAttribute("userid")).thenReturn(_FRIEND_ID_);
		int id = idees.addIdea(_FRIEND_ID_, "ma nouvelle idée", "", 1, null, null);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		int notifId = notif.addNotification(_FRIEND_ID_, new NotifAskIfIsUpToDate(firefox, idee));

		AbstractNotification n = notif.getNotification(notifId);
		String text = n.getText();
		String ideaId = text.substring(	text.indexOf("nfirmation_est_a_jour?idee=") + "nfirmation_est_a_jour?idee=".length(),
										text.indexOf("\">Oui !</a></li><li>Non"));
		ideaId = new String(ideaId.getBytes("UTF-8"), "ISO-8859-1");

		when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
		when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(ideaId);

		doTestGet(request, response);

		// Ménage
		idees.remove(id);
	}

}
