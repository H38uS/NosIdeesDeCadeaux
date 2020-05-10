package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class GetIdeaServiceTestWebApp extends AbstractTestServletWebApp {

    public GetIdeaServiceTestWebApp() {
        super(new GetIdeaService());
    }

    @Test
    public void testSucces() throws SQLException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(GetIdeaService.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        doTestGet();
    }
}