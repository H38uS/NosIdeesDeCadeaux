package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.database.NoRowsException;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeeQuestions;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TestIdeaQuestion extends AbstractTestServlet {

    public TestIdeaQuestion() {
        super(new IdeeQuestions());
    }

    @Test
    public void testGetQuestions() throws SQLException, NoRowsException {

        int id = IdeesRepository.addIdea(firefox, "avec questions", null, 0, null, null, null);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id);
        QuestionsRepository.addComment(_FRIEND_ID_, id, "mon pti com'");

        int addByFriend = NotificationsRepository.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
        int newQuestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(newQuestion);

        when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(id + "");
        doTestGet();

        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(newQuestion);
        IdeesRepository.remove(id);
    }

    @Test
    public void testAjouterQuestion() throws SQLException {

        int ideaId = IdeesRepository.addIdea(friendOfFirefox, "sans questions", null, 0, null, null, null);
        assertEquals(0, QuestionsRepository.getCommentsOn(ideaId).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId + "");
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(1, QuestionsRepository.getCommentsOn(ideaId).size());
        IdeesRepository.remove(ideaId);
    }

    @Test
    public void testAjouterQuestionSurUneSurprise() throws SQLException {

        int ideaId = IdeesRepository.addIdea(friendOfFirefox, "sans questions", null, 0, null, firefox, firefox);
        assertEquals(0, QuestionsRepository.getCommentsOn(ideaId).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(ideaId + "");
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(0, QuestionsRepository.getCommentsOn(ideaId).size()); // Impossible de poser une question sur une surprise !
        IdeesRepository.remove(ideaId);
    }

}
