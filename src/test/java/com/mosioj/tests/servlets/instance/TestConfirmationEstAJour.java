package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.servlets.controllers.compte.MyNotifications;
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
		Idee idee = idees.getIdea(id);
		
		int notifId = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
		assertEquals(1, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));

		when(request.getRequestDispatcher(MyNotifications.URL)).thenReturn(dispatcher);
		when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id+"");
		doTestGet(request, response);

		assertEquals(0, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
	}

}
