package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.idees.IdeaComments;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class TestIdeaComment extends AbstractTestServlet {

    public TestIdeaComment() {
        super(new IdeaComments());
    }

    @Test
    public void test() throws SQLException {

        int id = idees.addIdea(friendOfFirefox, "avec commentaire", null, 0, null, null, null);
        Idee idee = idees.getIdeaWithoutEnrichment(id);
        comments.addComment(_OWNER_ID_, id, "mon pti com'");

        int newComment = notif.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        assertNotifDoesExists(newComment);

        when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeaComments.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestGet();

        assertNotifDoesNotExists(newComment);
        idees.remove(id);
    }

}
