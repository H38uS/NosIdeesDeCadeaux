package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeaComments;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

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

        int newComment = NType.NEW_COMMENT_ON_IDEA.with(firefox, idea).sendItTo(firefox);
        assertNotifDoesExists(newComment);

        when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(IdeaComments.IDEA_ID_PARAM)).thenReturn(String.valueOf(idea.getId()));
        doTestGet();

        assertNotifDoesNotExists(newComment);
        IdeesRepository.remove(idea);
    }

}
