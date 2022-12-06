package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceAjouterParentTest extends AbstractTestServletWebApp {

    public ServiceAjouterParentTest() {
        super(new ServiceAjouterParent());
    }

    @Before
    public void initialize() {
        ParentRelationshipRepository.deleteParents(firefox);
    }

    @After
    public void tearDown() {
        ParentRelationshipRepository.deleteParents(firefox);
    }

    @Test
    public void testAjoutSucces() {

        bindRequestParam(ServiceAjouterParent.NAME_OR_EMAIL, "tesT@toto.Com");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals("Test@toto.com", resp.getMessage());
    }

    @Test
    public void testIncorrectEmail() {

        bindRequestParam(ServiceAjouterParent.NAME_OR_EMAIL, "");

        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("L'ajout du parent a échoué : il n'existe pas (ou trop) de compte pour le nom ou l'email passé en paramètre.",
                     resp.getMessage());
    }

    @Test
    public void testDejaAjoute() {

        bindRequestParam(ServiceAjouterParent.NAME_OR_EMAIL, "test@toto.com");

        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertFalse(resp.isOK());

        assertEquals("L'ajout du parent a échoué : il existe déjà.", resp.getMessage());
    }

    @Test
    public void shouldNotBePossibleToAddOurself() {

        bindRequestParam(ServiceAjouterParent.NAME_OR_EMAIL, firefox.getEmail());

        StringServiceResponse resp = doTestServicePost();
        assertFalse(resp.isOK());

        assertEquals("Vous ne pouvez pas vous ajouter vous-même...", resp.getMessage());
    }
}