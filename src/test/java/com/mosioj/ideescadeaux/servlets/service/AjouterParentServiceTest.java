package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AjouterParentServiceTest extends AbstractTestServlet {

    public AjouterParentServiceTest() {
        super(new AjouterParentService());
    }

    @After
    public void tearDown() {
        parents.deleteParents(firefox);
    }

    @Test
    public void testAjoutSucces() {

        parents.deleteParents(firefox);
        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("tesT@toto.Com");

        ServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals("Test@toto.com", resp.getMessage());
    }

    @Test
    public void testIncorrectEmail() {

        parents.deleteParents(firefox);
        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("");

        ServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.", resp.getMessage());
    }

    @Test
    public void testDejaAjoute() {

        parents.deleteParents(firefox);
        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("test@toto.com");

        ServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());

        assertEquals("L'ajout du parent a échoué : il existe déjà.", resp.getMessage());
    }
}