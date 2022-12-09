package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServiceSousReserverTest extends AbstractTestServletWebApp {


    public ServiceSousReserverTest() {
        super(new ServiceSousReserver());
    }

    @Test
    public void canSubBookFromTwoUsers() {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).get(0);
        IdeesRepository.toutDereserver(idee);
        bindPostRequestParam("idee", String.valueOf(idee.getId()));
        bindPostRequestParam("comment", "Un commentaire");

        // When
        doTestServicePost();
        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        List<SousReservationEntity> resa = SousReservationRepository.getSousReservation(idee.getId());
        assertEquals(2, resa.size());
    }

    @Test
    public void cannotSubBookTwice() {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).get(0);
        IdeesRepository.toutDereserver(idee);
        bindPostRequestParam("idee", String.valueOf(idee.getId()));
        bindPostRequestParam("comment", "Un commentaire");
        doTestServicePost();

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertFalse(resp.isOK());
        assertEquals("L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.", resp.getMessage());
        List<SousReservationEntity> resa = SousReservationRepository.getSousReservation(idee.getId());
        assertEquals(1, resa.size());
    }

}