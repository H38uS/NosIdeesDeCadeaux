package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.idees.IdeeQuestions;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TestIdeaQuestion extends AbstractTestServlet {

    public TestIdeaQuestion() {
        super(new IdeeQuestions());
    }

    @Test
    public void testGetQuestions() throws SQLException {

        int id = idees.addIdea(firefox, "avec questions", null, 0, null, null, null);
        Idee idee = idees.getIdeaWithoutEnrichment(id);
        questions.addComment(_FRIEND_ID_, id, "mon pti com'");

        int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
        int newQuestion = notif.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(newQuestion);

        when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestGet();

        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(newQuestion);
        idees.remove(id);
    }

    @Test
    public void testAjouterQuestion() throws SQLException {

        int ideaId = idees.addIdea(friendOfFirefox, "sans questions", null, 0, null, null, null);
        assertEquals(0, questions.getCommentsOn(ideaId).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId + "");
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(1, questions.getCommentsOn(ideaId).size());
        idees.remove(ideaId);
    }

    @Test
    public void testAjouterQuestionSurUneSurprise() throws SQLException {

        int ideaId = idees.addIdea(friendOfFirefox, "sans questions", null, 0, null, firefox, firefox);
        assertEquals(0, questions.getCommentsOn(ideaId).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId + "");
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(0, questions.getCommentsOn(ideaId).size()); // Impossible de poser une question sur une surprise !
        idees.remove(ideaId);
    }

}
