package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class GetIdeaOfFriendServiceTestWebApp extends AbstractTestServletWebApp {

    public GetIdeaOfFriendServiceTestWebApp() {
        super(new GetIdeaOfFriendService());
    }

    @Test
    public void testSucces() throws SQLException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(GetIdeaOfFriendService.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        doTestGet();
    }
}