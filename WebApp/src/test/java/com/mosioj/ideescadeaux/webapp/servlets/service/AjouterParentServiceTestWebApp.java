package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AjouterParentServiceTestWebApp extends AbstractTestServletWebApp {

    public AjouterParentServiceTestWebApp() {
        super(new AjouterParentService());
    }

    @Before
    public void initialize() throws SQLException {
        ParentRelationshipRepository.deleteParents(firefox);
    }

    @After
    public void tearDown() throws SQLException {
        ParentRelationshipRepository.deleteParents(firefox);
    }

    @Test
    public void testAjoutSucces() {

        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("tesT@toto.Com");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals("Test@toto.com", resp.getMessage());
    }

    @Test
    public void testIncorrectEmail() {

        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.",
                     resp.getMessage());
    }

    @Test
    public void testDejaAjoute() {

        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn("test@toto.com");

        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());

        assertEquals("L'ajout du parent a échoué : il existe déjà.", resp.getMessage());
    }

    @Test
    public void shouldNotBePossibleToAddOurself() {

        when(request.getParameter(AjouterParentService.NAME_OR_EMAIL)).thenReturn(firefox.getEmail());

        StringServiceResponse resp = doTestServicePost();
        assertFalse(resp.isOK());

        assertEquals("Vous ne pouvez pas vous ajouter vous-même...", resp.getMessage());
    }
}