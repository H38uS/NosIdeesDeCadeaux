package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestServiceUpdateNotificationParameter extends AbstractTestServletWebApp {

    public TestServiceUpdateNotificationParameter() {
        super(new ServiceUpdateNotificationParameter());
    }

    @Test
    public void testInvalidValue() {

        bindPostRequestParam("name", NType.FRIENDSHIP_DROPPED.toString());
        bindPostRequestParam("value", "toto");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Valeur inconnue...", resp.getMessage());
    }

    @Test
    public void testInvalidNotificationName() {

        bindPostRequestParam("name", "toto");
        bindPostRequestParam("value", NotificationActivation.SITE.toString());

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Type de notification inconnu...", resp.getMessage());
    }

    @Test
    public void testInvalidParameterAndValue() {

        bindPostRequestParam("name", "toto");
        bindPostRequestParam("value", "toto");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
    }

    @Test
    public void testSucces() {

        bindPostRequestParam("name", NType.FRIENDSHIP_DROPPED.toString());
        bindPostRequestParam("value", NotificationActivation.SITE.toString());

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
    }
}