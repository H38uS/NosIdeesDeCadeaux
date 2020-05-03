package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static org.mockito.Mockito.when;

public class RechercherPersonneServiceTestWebApp extends AbstractTestServletWebApp {

    public RechercherPersonneServiceTestWebApp() {
        super(new RechercherPersonneService());
    }

    @Test
    public void testSuccess() {
        when(request.getRequestDispatcher(RechercherPersonneService.FORM_URL_SERVICE)).thenReturn(dispatcher);
        when(request.getParameter("name")).thenReturn("est@toto.co");
        when(request.getParameter("only_non_friend")).thenReturn("nop");
        when(request.getParameter("name")).thenReturn("");

        doTestGet();
    }

    @Test
    public void testSuccessOnlyNonFriends() {
        when(request.getRequestDispatcher(RechercherPersonneService.FORM_URL_SERVICE)).thenReturn(dispatcher);
        when(request.getParameter("name")).thenReturn("est@toto.co");
        when(request.getParameter("only_non_friend")).thenReturn("on");
        when(request.getParameter("name")).thenReturn("");

        doTestGet();
    }
}