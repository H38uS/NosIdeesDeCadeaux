package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class ServiceRechercherPersonneTestWebApp extends AbstractTestServletWebApp {

    public ServiceRechercherPersonneTestWebApp() {
        super(new ServiceRechercherPersonne());
    }

    @Test
    public void testSuccess() {
        when(request.getRequestDispatcher(ServiceRechercherPersonne.FORM_URL_SERVICE)).thenReturn(dispatcher);
        when(request.getParameter("name")).thenReturn("est@toto.co");
        when(request.getParameter("only_non_friend")).thenReturn("nop");
        when(request.getParameter("name")).thenReturn("");

        doTestGet();
    }

    @Test
    public void testSuccessOnlyNonFriends() {
        when(request.getRequestDispatcher(ServiceRechercherPersonne.FORM_URL_SERVICE)).thenReturn(dispatcher);
        when(request.getParameter("name")).thenReturn("est@toto.co");
        when(request.getParameter("only_non_friend")).thenReturn("on");
        when(request.getParameter("name")).thenReturn("");

        doTestGet();
    }
}