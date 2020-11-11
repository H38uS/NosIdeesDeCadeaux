package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeeQuestions;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TestIdeaQuestionWebApp extends AbstractTestServletWebApp {

    public TestIdeaQuestionWebApp() {
        super(new IdeeQuestions());
    }

    @Test
    public void testGetQuestions() throws SQLException {

        Idee idee = IdeesRepository.addIdea(firefox, "avec questions", null, 0, null, null, null);
        QuestionsRepository.addComment(_FRIEND_ID_, idee.getId(), "mon pti com'");

        int addByFriend = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifIdeaAddedByFriend(moiAutre, idee));
        int newQuestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifNewQuestionOnIdea(friendOfFirefox,
                                                                                             idee,
                                                                                             true));
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(newQuestion);

        when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        doTestGet();

        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(newQuestion);
        IdeesRepository.remove(idee);
    }

    @Test
    public void testAjouterQuestion() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "sans questions", null, 0, null, null, null);
        assertEquals(0, QuestionsRepository.getCommentsOn(idee.getId()).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(1, QuestionsRepository.getCommentsOn(idee.getId()).size());
        IdeesRepository.remove(idee);
    }

    @Test
    public void testAjouterQuestionSurUneSurprise() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "sans questions", null, 0, null, firefox, firefox);
        assertEquals(0, QuestionsRepository.getCommentsOn(idee.getId()).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(0,
                     QuestionsRepository.getCommentsOn(idee.getId())
                                        .size()); // Impossible de poser une question sur une surprise !
        IdeesRepository.remove(idee);
    }

}
