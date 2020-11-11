package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeaComments;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class TestIdeaCommentWebApp extends AbstractTestServletWebApp {

    public TestIdeaCommentWebApp() {
        super(new IdeaComments());
    }

    @Test
    public void test() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "avec commentaire", null, 0, null, null, null);
        CommentsRepository.addComment(_OWNER_ID_, idee.getId(), "mon pti com'");

        int newComment = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        assertNotifDoesExists(newComment);

        when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeaComments.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        doTestGet();

        assertNotifDoesNotExists(newComment);
        IdeesRepository.remove(idee);
    }

}
