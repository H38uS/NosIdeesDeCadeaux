package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class GetIdeaOfFriendServiceTest extends AbstractTestServlet {

    public GetIdeaOfFriendServiceTest() {
        super(new GetIdeaOfFriendService());
    }

    @Test
    public void testSucces() throws SQLException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(GetIdeaOfFriendService.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");
        when(request.getRequestDispatcher(GetIdeaOfFriendService.VIEW_PAGE_URL)).thenReturn(dispatcher);

        doTestGet();
    }
}