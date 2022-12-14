package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.Question;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceSupprimerQuestionsTest extends AbstractTestServletWebApp {

    public ServiceSupprimerQuestionsTest() {
        super(new ServiceSupprimerQuestions());
    }

    @Test
    public void testNominal() {
        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withOwner(firefox).withText("blablabla"));
        Question question = QuestionsRepository.addQuestion(friendOfFirefox, idee, "a question!");
        bindPostRequestParam(ServiceSupprimerQuestions.MESSAGE_ID_PARAMETER, question.getId());
        assertEquals(List.of(question), QuestionsRepository.getQuestionsOn(idee));

        // When
        setConnectedUserTo(friendOfFirefox);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        assertEquals(Collections.emptyList(), QuestionsRepository.getQuestionsOn(idee));
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testCannotRemoveOtherComments() {
        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withOwner(firefox).withText("blablabla"));
        Question question = QuestionsRepository.addQuestion(friendOfFirefox, idee, "a question!");
        bindPostRequestParam(ServiceSupprimerQuestions.MESSAGE_ID_PARAMETER, question.getId());
        assertEquals(List.of(question), QuestionsRepository.getQuestionsOn(idee));

        // When
        setConnectedUserTo(jo3);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertFalse(resp.isOK());
        assertEquals(List.of(question), QuestionsRepository.getQuestionsOn(idee));
        IdeesRepository.trueRemove(idee);
    }
}