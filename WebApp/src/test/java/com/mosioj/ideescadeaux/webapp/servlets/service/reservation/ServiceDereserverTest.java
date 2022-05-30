package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceDereserverTest extends AbstractTestServletWebApp {

    public ServiceDereserverTest() {
        super(new ServiceDereserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}