package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.GroupIdeaDetails;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class TestGroupIdeaDetailsWebApp extends AbstractTestServletWebApp {

    public TestGroupIdeaDetailsWebApp() {
        super(new GroupIdeaDetails());
    }

    @Test
    public void testGet() throws SQLException {

        Idee idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idea.getId(), group.getId());

        Notification notifGroupSuggestion = NType.GROUP_IDEA_SUGGESTION.with(firefox, idea, group);
        int groupSuggestion = notifGroupSuggestion.sendItTo(firefox);
        assertNotifDoesExists(groupSuggestion);

        when(request.getRequestDispatcher(GroupIdeaDetails.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        doTestGet();
        assertNotifDoesNotExists(groupSuggestion);

        IdeesRepository.remove(idea);
    }

}
