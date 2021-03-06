package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceJeLaVeuxEncoreTest extends AbstractTestServletWebApp {

    public ServiceJeLaVeuxEncoreTest() {
        super(new ServiceJeLaVeuxEncore());
    }

    @Test
    public void groupsShouldBeDeleted() throws SQLException {

        // Création de l'idée et réservation par un groupe
        Idee idea = IdeesRepository.getIdeasOf(firefox).stream().findAny().orElseThrow(SQLException::new);
        IdeesRepository.toutDereserver(idea);
        IdeaGroup group = GroupIdeaRepository.createAGroup(20, 10, friendOfFirefox);
        IdeesRepository.bookByGroup(idea, group);

        // Vérification que c'est bien réservé par un groupe
        assertEquals(BookingInformation.BookingType.GROUP,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));

        // Appel au service
        when(request.getParameter(ServiceJeLaVeuxEncore.IDEA_ID_PARAM)).thenReturn(idea.getId() + "");
        StringServiceResponse resp = doTestServicePost();

        // Vérification
        assertTrue(resp.isOK());
        assertFalse(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
        assertEquals(BookingInformation.BookingType.NONE,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));
    }

    @Test
    public void sousreservationMustBeKept() throws SQLException {

        // Création de l'idée et réservation par une sous-réservation
        Idee idea = IdeesRepository.getIdeasOf(firefox).stream().findAny().orElseThrow(SQLException::new);
        IdeesRepository.toutDereserver(idea);
        IdeesRepository.sousReserver(idea);
        SousReservationRepository.sousReserver(idea.getId(), friendOfFirefox.getId(), "voilou");

        // Vérification que c'est bien réservé en tant que réservation partielle
        assertTrue(SousReservationRepository.getSousReservation(idea.getId()).size() > 0);
        assertEquals(BookingInformation.BookingType.PARTIAL,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));

        // Appel au service
        when(request.getParameter(ServiceJeLaVeuxEncore.IDEA_ID_PARAM)).thenReturn(idea.getId() + "");
        StringServiceResponse resp = doTestServicePost();

        // Vérification
        assertTrue(resp.isOK());
        assertTrue(SousReservationRepository.getSousReservation(idea.getId()).size() > 0);
        assertEquals(BookingInformation.BookingType.PARTIAL,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));
    }

    @Test
    public void bookingShouldBeRemoved() throws SQLException {

        // Création de l'idée et réservation par un groupe
        Idee idea = IdeesRepository.getIdeasOf(firefox).stream().findAny().orElseThrow(SQLException::new);
        IdeesRepository.toutDereserver(idea);
        IdeesRepository.reserver(idea, friendOfFirefox);

        // Vérification que c'est bien réservé par un groupe
        assertEquals(BookingInformation.BookingType.SINGLE_PERSON,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));

        // Appel au service
        when(request.getParameter(ServiceJeLaVeuxEncore.IDEA_ID_PARAM)).thenReturn(idea.getId() + "");
        StringServiceResponse resp = doTestServicePost();

        // Vérification
        assertTrue(resp.isOK());
        assertEquals(BookingInformation.BookingType.NONE,
                     IdeesRepository.getIdea(idea.getId())
                                    .map(Idee::getBookingType)
                                    .orElseThrow(SQLException::new));
    }
}