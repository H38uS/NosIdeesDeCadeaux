package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.servlets.controllers.idees.IdeeQuestions;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestIdeaQuestion extends AbstractTestServlet {

	public TestIdeaQuestion() {
		super(new IdeeQuestions());
	}

	@Test
	public void testGetQuestions() throws SQLException, ServletException, IOException {
		
		int id = idees.addIdea(firefox, "avec questions", null, 0, null, null, null);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		questions.addComment(_FRIEND_ID_, id, "mon pti com'");

		int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
		int newQuestion = notif.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
		assertNotifDoesExists(addByFriend);
		assertNotifDoesExists(newQuestion);

		when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
		when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(id+"");
		doTestGet(request, response);

		assertNotifDoesNotExists(addByFriend);
		assertNotifDoesNotExists(newQuestion);
		idees.remove(id);
	}

	@Test
	public void testAjouterQuestion() throws SQLException, ServletException, IOException {
		
		int ideaId = idees.addIdea(friendOfFirefox, "sans questions", null, 0, null, null, null);
		assertEquals(0, questions.getCommentsOn(ideaId).size());
		
		when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId+"");
		when(request.getParameter("text")).thenReturn("Voilou voilou");
		doTestPost(request, response);
		
		assertEquals(1, questions.getCommentsOn(ideaId).size());
		idees.remove(ideaId);
	}

	@Test
	public void testAjouterQuestionSurUneSurprise() throws SQLException, ServletException, IOException {
		
		int ideaId = idees.addIdea(friendOfFirefox, "sans questions", null, 0, null, firefox, firefox);
		assertEquals(0, questions.getCommentsOn(ideaId).size());
		
		when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId+"");
		when(request.getParameter("text")).thenReturn("Voilou voilou");
		doTestPost(request, response);
		
		assertEquals(0, questions.getCommentsOn(ideaId).size()); // Impossible de poser une question sur une surprise !
		idees.remove(ideaId);
	}

}
