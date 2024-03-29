package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.Question;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.IDEA_ADDED_BY_FRIEND;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_TO_OWNER;
import static org.junit.Assert.*;

public class ServiceQuestionsTest extends AbstractTestServletWebApp {

    public ServiceQuestionsTest() {
        super(new ServiceQuestions());
    }

    @Test
    public void testAjouterQuestion() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans questions")
                                                    .withPriority(p));
        assertEquals(0, QuestionsRepository.getQuestionsOn(idee).size());
        bindPostRequestParam(ServiceQuestions.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        final List<Question> found = QuestionsRepository.getQuestionsOn(idee);
        assertEquals(1, found.size());
        assertEquals("Voilou voilou", found.get(0).getText());
        assertEquals("<p>Voilou voilou</p>\n", found.get(0).getHtml());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testAjouterQuestionSurUneSurprise() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans questions")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));
        assertEquals(0, QuestionsRepository.getQuestionsOn(idee).size());
        bindPostRequestParam(ServiceQuestions.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertFalse(resp.isOK());
        // Impossible de poser une question sur une surprise !
        assertEquals(0, QuestionsRepository.getQuestionsOn(idee).size());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testSpecialCharacter() {

        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans questions"));
        assertEquals(0, QuestionsRepository.getQuestionsOn(idee).size());
        bindPostRequestParam(ServiceQuestions.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou àîôûé\"-'(à'");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        final List<Question> found = QuestionsRepository.getQuestionsOn(idee);
        assertEquals(1, found.size());
        assertEquals("Voilou voilou àîôûé\"-'(à'", found.get(0).getText());
        assertEquals("<p>Voilou voilou àîôûé&quot;-'(à'</p>\n", found.get(0).getHtml());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testNotificationDeletion() {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElse(null);
        assert p != null;
        Idee idea = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("avec questions")
                                                    .withPriority(p));
        QuestionsRepository.addQuestion(friendOfFirefox, idea, "mon pti com'");

        Notification addByFriend = IDEA_ADDED_BY_FRIEND.with(moiAutre, idea).sendItTo(firefox);
        Notification newQuestion = NEW_QUESTION_TO_OWNER.with(friendOfFirefox, idea).sendItTo(firefox);
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(newQuestion);

        // When
        bindGetRequestParam(ServiceQuestions.IDEA_ID_PARAM, idea.getId());
        ServiceResponse<?> resp = doTestServiceGet(ServiceResponse.class);

        // Then
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(newQuestion);
        IdeesRepository.trueRemove(idea);
    }
}