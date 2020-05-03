package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.UpdateNotificationParameter;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestUpdateNotificationParameterWebApp extends AbstractTestServletWebApp {

    public TestUpdateNotificationParameterWebApp() {
        super(new UpdateNotificationParameter());
    }

    @Test
    public void testPost() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("REJECTED_FRIENDSHIP");
        when(request.getParameter("value")).thenReturn("EMAIL");
        instance.doPost(request, response);
        verify(request).getParameter("name");
        verify(request).getParameter("value");
    }

}
