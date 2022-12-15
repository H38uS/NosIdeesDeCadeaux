package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceCommentsTest extends AbstractTestServletWebApp {


    public ServiceCommentsTest() {
        super(new ServiceComments());
    }

    @Test
    public void testAjouterComment() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans comments")
                                                    .withPriority(p));
        assertEquals(0, CommentsRepository.getCommentsOn(idee).size());
        bindPostRequestParam(ServiceComments.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        final var found = CommentsRepository.getCommentsOn(idee);
        assertEquals(1, found.size());
        assertEquals("Voilou voilou", found.get(0).getText());
        assertEquals("<p>Voilou voilou</p>\n", found.get(0).getHtml());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testAjouterCommentSurUneSurprise() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans comments")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));
        assertEquals(0, CommentsRepository.getCommentsOn(idee).size());
        bindPostRequestParam(ServiceComments.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        assertEquals(1, CommentsRepository.getCommentsOn(idee).size());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void testSpecialCharacter() {

        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("sans comments"));
        assertEquals(0, CommentsRepository.getCommentsOn(idee).size());
        bindPostRequestParam(ServiceComments.IDEA_ID_PARAM, idee.getId());
        bindPostRequestParam("text", "Voilou voilou àîôûé\"-'(à'");

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        final var found = CommentsRepository.getCommentsOn(idee);
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
                                                    .withOwner(friendOfFirefox)
                                                    .withText("avec comments")
                                                    .withPriority(p));
        CommentsRepository.addComment(jo3, idea, "mon pti com'");

        Notification newComment = NType.NEW_COMMENT_ON_IDEA.with(firefox, idea).sendItTo(firefox);
        assertNotifDoesExists(newComment);

        // When
        bindGetRequestParam(ServiceComments.IDEA_ID_PARAM, idea.getId());
        ServiceResponse<?> resp = doTestServiceGet(ServiceResponse.class);

        // Then
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(newComment);
        IdeesRepository.trueRemove(idea);
    }
}