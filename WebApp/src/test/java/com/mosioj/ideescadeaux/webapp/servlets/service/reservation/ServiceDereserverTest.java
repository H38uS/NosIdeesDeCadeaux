package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServiceDereserverTest extends AbstractTestServletWebApp {

    public ServiceDereserverTest() {
        super(new ServiceDereserver());
    }

    @Test
    public void testSuccess() {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).stream().findFirst().orElse(null);
        assert idee != null;
        bindPostRequestParam(ServiceDereserver.IDEA_ID_PARAM, idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}