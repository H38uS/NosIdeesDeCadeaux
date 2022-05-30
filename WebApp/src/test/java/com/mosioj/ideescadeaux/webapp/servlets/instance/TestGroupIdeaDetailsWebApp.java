package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
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

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idea = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("toto")
                                                    .withPriority(p));
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, moiAutre);
        IdeesRepository.bookByGroup(idea, group);

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
