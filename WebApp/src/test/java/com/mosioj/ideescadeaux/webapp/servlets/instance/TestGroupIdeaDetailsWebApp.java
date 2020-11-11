package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
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
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idea.getId(), id);

        NotifGroupSuggestion notifGroupSuggestion = new NotifGroupSuggestion(firefox, id, idea);
        int groupSuggestion = NotificationsRepository.addNotification(_OWNER_ID_, notifGroupSuggestion);
        assertNotifDoesExists(groupSuggestion);

        when(request.getRequestDispatcher(GroupIdeaDetails.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        doTestGet();
        assertNotifDoesNotExists(groupSuggestion);

        IdeesRepository.remove(idea);
    }

}
