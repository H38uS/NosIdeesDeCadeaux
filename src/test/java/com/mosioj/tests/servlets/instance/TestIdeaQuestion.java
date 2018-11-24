package com.mosioj.tests.servlets.instance;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.servlets.controllers.idees.IdeaQuestion;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestIdeaQuestion extends AbstractTestServlet {

	public TestIdeaQuestion() {
		super(new IdeaQuestion());
	}

	@Test
	public void testGetQuestions() throws SQLException, ServletException, IOException {
		
		int id = idees.addIdea(_OWNER_ID_, "avec questions", null, 0, null, null, null);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		questions.addComment(_FRIEND_ID_, id, "mon pti com'");

		int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
		int newQuestion = notif.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
		assertNotifDoesExists(addByFriend);
		assertNotifDoesExists(newQuestion);

		when(request.getRequestDispatcher(IdeaQuestion.VIEW_PAGE_URL)).thenReturn(dispatcher);
		when(request.getParameter(IdeaQuestion.IDEA_ID_PARAM)).thenReturn(id+"");
		doTestGet(request, response);

		assertNotifDoesNotExists(addByFriend);
		assertNotifDoesNotExists(newQuestion);
		idees.remove(id);
	}

}
