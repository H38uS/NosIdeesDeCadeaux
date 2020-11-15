package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceUpdateNotificationParameterTestWebApp extends AbstractTestServletWebApp {

    public ServiceUpdateNotificationParameterTestWebApp() {
        super(new ServiceUpdateNotificationParameter());
    }

    @Test
    public void testInvalidValue() {

        when(request.getParameter("name")).thenReturn(NType.FRIENDSHIP_DROPPED.toString());
        when(request.getParameter("value")).thenReturn("toto");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Valeur inconnue...", resp.getMessage());
    }

    @Test
    public void testInvalidNotificationName() {

        when(request.getParameter("name")).thenReturn("toto");
        when(request.getParameter("value")).thenReturn(NotificationActivation.SITE.toString());

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Type de notification inconnu...", resp.getMessage());
    }

    @Test
    public void testInvalidParameterAndValue() {

        when(request.getParameter("name")).thenReturn("toto");
        when(request.getParameter("value")).thenReturn("toto");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
    }

    @Test
    public void testSucces() {

        when(request.getParameter("name")).thenReturn(NType.FRIENDSHIP_DROPPED.toString());
        when(request.getParameter("value")).thenReturn(NotificationActivation.SITE.toString());

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}