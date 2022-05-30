package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceSupprimerSurpriseTest extends AbstractTestServletWebApp {

    public ServiceSupprimerSurpriseTest() {
        super(new ServiceSupprimerSurprise());
    }

    @Test
    public void shouldNotBePossibleToDeleteOurSurpriseAddedByFriend() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("une surprise")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(friendOfFirefox)
                                                    .withCreatedBy(friendOfFirefox));
        when(request.getParameter(ServiceSupprimerSurprise.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));

        // Act
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertFalse(resp.isOK());
        assertEquals("Vous n'avez pas créé cette surprise.", resp.getMessage());
        assertTrue(IdeesRepository.getIdea(idee.getId()).isPresent());
        IdeesRepository.trueRemove(idee);
    }

    @Test
    public void shouldBePossibleToDeleteASupriseWeMade() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("une surprise")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));
        when(request.getParameter(ServiceSupprimerSurprise.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));

        // Act
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertTrue(resp.isOK());
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
    }

    @Test
    public void shouldNotBePossibleToDeleteNonSuprise() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("une surprise")
                                                    .withPriority(p)
                                                    .withCreatedBy(friendOfFirefox));
        when(request.getParameter(ServiceSupprimerSurprise.IDEA_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));

        // Act
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertFalse(resp.isOK());
        assertTrue(IdeesRepository.getIdea(idee.getId()).isPresent());
        IdeesRepository.trueRemove(idee);
    }
}