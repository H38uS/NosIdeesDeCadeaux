package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeeQuestions;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.IDEA_ADDED_BY_FRIEND;
import static com.mosioj.ideescadeaux.core.model.notifications.NType.NEW_QUESTION_TO_OWNER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class TestIdeaQuestionWebApp extends AbstractTestServletWebApp {

    public TestIdeaQuestionWebApp() {
        super(new IdeeQuestions());
    }

    @Test
    public void testGetQuestions() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idea = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("avec questions")
                                                    .withPriority(p));
        QuestionsRepository.addComment(_FRIEND_ID_, idea.getId(), "mon pti com'");

        int addByFriend = IDEA_ADDED_BY_FRIEND.with(moiAutre, idea).sendItTo(firefox);
        int newQuestion = NEW_QUESTION_TO_OWNER.with(friendOfFirefox, idea).sendItTo(firefox);
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(newQuestion);

        when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(String.valueOf(idea.getId()));
        doTestGet();

        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(newQuestion);
        IdeesRepository.trueRemove(idea);
    }

    @Test
    public void testAjouterQuestion() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans questions")
                                                    .withPriority(p));
        assertEquals(0, QuestionsRepository.getCommentsOn(idee.getId()).size());

        when(request.getParameter(IdeeQuestions.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter("text")).thenReturn("Voilou voilou");
        doTestPost();

        assertEquals(1, QuestionsRepository.getCommentsOn(idee.getId()).size());
        IdeesRepository.remove(idee);
    }

    @Test
    public void testAjouterQuestionSurUneSurprise() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans questions")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));
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
