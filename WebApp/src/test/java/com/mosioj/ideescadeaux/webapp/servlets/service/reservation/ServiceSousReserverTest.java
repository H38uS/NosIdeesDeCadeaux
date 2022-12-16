package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.SousReservation;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceSousReserverTest extends AbstractTestServletWebApp {


    public ServiceSousReserverTest() {
        super(new ServiceSousReserver());
    }

    @Test
    public void canSubBookFromTwoUsers() {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).stream().findFirst().orElse(null);
        IdeesRepository.toutDereserver(idee);
        assert idee != null;
        bindPostRequestParam("idee", String.valueOf(idee.getId()));
        bindPostRequestParam("comment", "Un commentaire");

        // When
        doTestServicePost();
        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        List<SousReservation> resa = SousReservationRepository.getSousReservation(idee);
        assertEquals(2, resa.size());
    }

    @Test
    public void canSubBookTwice() {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox).stream().findFirst().orElse(null);
        IdeesRepository.toutDereserver(idee);
        assert idee != null;
        bindPostRequestParam("idee", String.valueOf(idee.getId()));
        bindPostRequestParam("comment", "Un commentaire");
        doTestServicePost();

        // When
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        List<SousReservation> resa = SousReservationRepository.getSousReservation(idee);
        assertEquals(2, resa.size());
    }

}