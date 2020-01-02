package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeaComments;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class TestIdeaComment extends AbstractTestServlet {

    public TestIdeaComment() {
        super(new IdeaComments());
    }

    @Test
    public void test() throws SQLException {

        int id = IdeesRepository.addIdea(friendOfFirefox, "avec commentaire", null, 0, null, null, null);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id).orElseThrow(SQLException::new);
        CommentsRepository.addComment(_OWNER_ID_, id, "mon pti com'");

        int newComment = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        assertNotifDoesExists(newComment);

        when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeaComments.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestGet();

        assertNotifDoesNotExists(newComment);
        IdeesRepository.remove(id);
    }

}
