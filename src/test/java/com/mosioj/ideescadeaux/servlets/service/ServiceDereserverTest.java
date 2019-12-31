package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceDereserverTest extends AbstractTestServlet {

    public ServiceDereserverTest() {
        super(new ServiceDereserver());
    }

    @Test
    public void testSuccess() throws SQLException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}