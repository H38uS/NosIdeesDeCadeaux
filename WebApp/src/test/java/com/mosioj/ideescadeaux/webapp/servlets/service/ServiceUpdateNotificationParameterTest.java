package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceUpdateNotificationParameterTest extends AbstractTestServlet {

    public ServiceUpdateNotificationParameterTest() {
        super(new ServiceUpdateNotificationParameter());
    }

    @Test
    public void testInvalidValue() {

        when(request.getParameter("name")).thenReturn(NotificationType.FRIENDSHIP_DROPPED.toString());
        when(request.getParameter("value")).thenReturn("toto");

        ServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Valeur inconnue...", resp.getMessage());
    }

    @Test
    public void testInvalidNotificationName() {

        when(request.getParameter("name")).thenReturn("toto");
        when(request.getParameter("value")).thenReturn(NotificationActivation.SITE.toString());

        ServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Type de notification inconnu...", resp.getMessage());
    }

    @Test
    public void testInvalidParameterAndValue() {

        when(request.getParameter("name")).thenReturn("toto");
        when(request.getParameter("value")).thenReturn("toto");

        ServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
    }

    @Test
    public void testSucces() {

        when(request.getParameter("name")).thenReturn(NotificationType.FRIENDSHIP_DROPPED.toString());
        when(request.getParameter("value")).thenReturn(NotificationActivation.SITE.toString());

        ServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}