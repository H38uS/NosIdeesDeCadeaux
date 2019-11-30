package com.mosioj.tests.servlets.instance;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.IdeaComments;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestIdeaComment extends AbstractTestServlet {

	public TestIdeaComment() {
		super(new IdeaComments());
	}

	@Test
	public void test() throws SQLException, ServletException, IOException {
		
		int id = idees.addIdea(friendOfFirefox, "avec commentaire", null, 0, null, null, null);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		comments.addComment(_OWNER_ID_, id, "mon pti com'");

		int newComment = notif.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
		assertNotifDoesExists(newComment);

		when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);
		when(request.getParameter(IdeaComments.IDEA_ID_PARAM)).thenReturn(id+"");
		doTestGet(request, response);

		assertNotifDoesNotExists(newComment);
		idees.remove(id);
	}

}
