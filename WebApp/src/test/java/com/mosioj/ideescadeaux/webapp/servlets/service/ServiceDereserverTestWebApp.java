package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceDereserverTestWebApp extends AbstractTestServletWebApp {

    public ServiceDereserverTestWebApp() {
        super(new ServiceDereserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}