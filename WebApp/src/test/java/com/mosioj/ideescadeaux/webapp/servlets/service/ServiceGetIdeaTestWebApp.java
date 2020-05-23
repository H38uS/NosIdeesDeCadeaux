package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class ServiceGetIdeaTestWebApp extends AbstractTestServletWebApp {

    public ServiceGetIdeaTestWebApp() {
        super(new ServiceGetIdea());
    }

    @Test
    public void testSucces() throws SQLException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(ServiceGetIdea.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        doTestGet();
    }
}