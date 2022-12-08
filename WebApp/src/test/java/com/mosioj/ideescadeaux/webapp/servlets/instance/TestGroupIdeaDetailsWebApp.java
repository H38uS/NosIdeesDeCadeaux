package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.GroupIdeaDetails;
import org.junit.Test;

import java.sql.SQLException;

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
        Notification groupSuggestion = notifGroupSuggestion.sendItTo(firefox);
        assertNotifDoesExists(groupSuggestion);

        bindRequestParam(GroupIdeaDetails.GROUP_ID_PARAM, group.getId());
        doTestGet();
        assertNotifDoesNotExists(groupSuggestion);

        IdeesRepository.remove(idea);
    }

}
