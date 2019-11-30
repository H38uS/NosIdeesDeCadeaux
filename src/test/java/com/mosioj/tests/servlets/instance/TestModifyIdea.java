package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.servlets.controllers.idees.modification.ModifyIdea;
import com.mosioj.ideescadeaux.utils.database.NoRowsException;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestModifyIdea extends AbstractTestServlet {

	public TestModifyIdea() {
		super(new ModifyIdea());
	}

	@Test
	public void testModifyRemovesCorrectNotification() throws SQLException, NoRowsException, ServletException, IOException {

		int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
		String newText = "Idee modifiee le " + new Date();
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		assertFalse(newText.equals(idee.text));

		int notifId = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
		int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
		assertNotifDoesExists(notifId);
		assertNotifDoesExists(addByFriend);

		Map<String, String> param = new HashMap<String, String>();
		param.put("text", newText);
		param.put("type", "");
		param.put("priority", 2 + "");
		createMultiPartRequest(param);
		when(request.getParameter(ModifyIdea.IDEE_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		idee = idees.getIdeaWithoutEnrichment(id);
		assertEquals(newText, idee.text);
		assertNotifDoesNotExists(notifId);
		assertNotifDoesNotExists(addByFriend);
	}

	@Test
	public void testModifyIdeaTwiceWithBirthdaySoonShouldSendOnlyOneNotification() throws SQLException, IOException, NoRowsException {

		// Given the users birthday is in 4 days...
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 4);
		firefox.birthday = new java.sql.Date(cal.getTime().getTime());
		users.update(firefox);

		// ... and the friend has no notifications yet, and notification activated
		notif.removeAll(_FRIEND_ID_);
		assertTrue(notif.getNotifications(	_FRIEND_ID_,
											NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
											ParameterName.USER_ID,
											_OWNER_ID_)
						.size() == 0);

		// ... and the user has an idea and a modification form
		int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
		Map<String, String> param = new HashMap<String, String>();
		param.put("text", "test notif when birthday is close");
		param.put("type", "");
		param.put("priority", 2 + "");
		when(request.getParameter(ModifyIdea.IDEE_ID_PARAM)).thenReturn(id + "");

		// Then a first modification creates a notification
		createMultiPartRequest(param);
		doTestPost(request, response);
		assertEquals(	1,
						notif	.getNotifications(_FRIEND_ID_,
												NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
												ParameterName.USER_ID,
												_OWNER_ID_)
								.size());

		// A second does not
		createMultiPartRequest(param);
		doTestPost(request, response);
		assertEquals(	1,
						notif	.getNotifications(_FRIEND_ID_,
												NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
												ParameterName.USER_ID,
												_OWNER_ID_)
								.size());
	}
}
