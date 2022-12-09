package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeaComments;
import org.junit.Test;

import java.sql.SQLException;

public class TestIdeaCommentWebApp extends AbstractTestServletWebApp {

    public TestIdeaCommentWebApp() {
        super(new IdeaComments());
    }

    @Test
    public void test() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idea = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("avec commentaire")
                                                    .withPriority(p));
        CommentsRepository.addComment(_OWNER_ID_, idea.getId(), "mon pti com'");

        Notification newComment = NType.NEW_COMMENT_ON_IDEA.with(firefox, idea).sendItTo(firefox);
        assertNotifDoesExists(newComment);

        bindGetRequestParam(IdeaComments.IDEA_ID_PARAM, idea.getId());
        doTestGet();

        assertNotifDoesNotExists(newComment);
        IdeesRepository.remove(idea);
    }

}
