package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.text.Comment;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceSupprimerCommentsTest extends AbstractTestServletWebApp {

    public ServiceSupprimerCommentsTest() {
        super(new ServiceSupprimerComments());
    }


    @Test
    public void testNominal() {
        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withOwner(firefox).withText("blablabla"));
        Comment comment = CommentsRepository.addComment(friendOfFirefox, idee, "a comment!");
        bindPostRequestParam(ServiceSupprimerComments.MESSAGE_ID_PARAMETER, comment.getId());
        assertEquals(List.of(comment), CommentsRepository.getCommentsOn(idee));

        // When
        setConnectedUserTo(friendOfFirefox);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        assertEquals(Collections.emptyList(), CommentsRepository.getCommentsOn(idee));
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testCannotRemoveOtherComments() {
        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withOwner(firefox).withText("blablabla"));
        Comment comment = CommentsRepository.addComment(friendOfFirefox, idee, "a comment!");
        bindPostRequestParam(ServiceSupprimerComments.MESSAGE_ID_PARAMETER, comment.getId());
        assertEquals(List.of(comment), CommentsRepository.getCommentsOn(idee));

        // When
        setConnectedUserTo(jo3);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertFalse(resp.isOK());
        assertEquals(List.of(comment), CommentsRepository.getCommentsOn(idee));
        IdeesRepository.trueRemove(idee);
    }
}