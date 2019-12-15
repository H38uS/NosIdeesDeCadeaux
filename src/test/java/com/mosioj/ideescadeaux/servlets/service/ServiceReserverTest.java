package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceReserverTest extends AbstractTestServlet {

    public ServiceReserverTest() {
        super(new ServiceReserver());
    }

    @Test
    public void testSuccess() throws SQLException {

        Idee idee = idees.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(ServiceDereserver.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}