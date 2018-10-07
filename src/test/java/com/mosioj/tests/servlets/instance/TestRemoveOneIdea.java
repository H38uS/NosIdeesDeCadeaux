package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.servlets.controllers.idees.modification.RemoveOneIdea;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestRemoveOneIdea extends AbstractTestServlet {

	public TestRemoveOneIdea() {
		super(new RemoveOneIdea());
	}

	@Test
	public void testDelete() throws SQLException, ServletException, IOException {

		int id = idees.addIdea(_OWNER_ID_, "generated", "", 0, null, null);
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(0, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));

		when(request.getParameter(RemoveOneIdea.IDEE_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));
	}

	@Test
	public void testDeleteWithGroupBooking() throws SQLException, ServletException, IOException {

		// Creation de l'idée
		int id = idees.addIdea(_OWNER_ID_, "generated", "", 0, null, null);
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

		// Creation du groupe
		GroupIdea g = new GroupIdea();
		int group = g.createAGroup(200, 10, 10);
		idees.bookByGroup(id, group);
		Idee idee = idees.getIdea(id);
		assertEquals(group, idee.getGroupKDO());
		assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
		assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));

		// Suppression
		when(request.getParameter(RemoveOneIdea.IDEE_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		// Validation que cela supprime tout
		assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
		assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));
	}

	@Test
	public void testUnderlyingNotificationAreWellRemoved() throws SQLException, ServletException, IOException {

		int id = idees.addIdea(_OWNER_ID_, "generated", "", 0, null, null);
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

		Idee idee = idees.getIdea(id);
		int isUpToDate = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
		int confirmedUpToDate = notif.addNotification(_FRIEND_ID_, new NotifConfirmedUpToDate(firefox, idee));
		int groupSuggestion = notif.addNotification(_FRIEND_ID_, new NotifGroupSuggestion(firefox, 0, idee));
		int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
		int modifiedWhenBDSoon = notif.addNotification(	_FRIEND_ID_,
														new NotifIdeaModifiedWhenBirthdayIsSoon(firefox, idee, false));
		int newComment = notif.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
		int newQuestion = notif.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
		int recurentUnbook = notif.addNotification(_FRIEND_ID_, new NotifRecurentIdeaUnbook(firefox, idee));

		assertTrue(isUpToDate > -1);
		assertNotifDoesExists(isUpToDate);
		assertNotifDoesExists(confirmedUpToDate);
		assertNotifDoesExists(groupSuggestion);
		assertNotifDoesExists(addByFriend);
		assertNotifDoesExists(modifiedWhenBDSoon);
		assertNotifDoesExists(newComment);
		assertNotifDoesExists(newQuestion);
		assertNotifDoesExists(recurentUnbook);

		// Suppression
		when(request.getParameter(RemoveOneIdea.IDEE_ID_PARAM)).thenReturn(id + "");
		doTestPost(request, response);

		assertNotifDoesNotExists(isUpToDate);
		assertNotifDoesNotExists(confirmedUpToDate);
		assertNotifDoesNotExists(groupSuggestion);
		assertNotifDoesNotExists(addByFriend);
		assertNotifDoesNotExists(modifiedWhenBDSoon);
		assertNotifDoesNotExists(newComment);
		assertNotifDoesNotExists(newQuestion);
		assertNotifDoesNotExists(recurentUnbook);
	}

}