package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.GroupIdeaDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.GROUP_IDEA_SUGGESTION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceParticipationGroupeTest extends AbstractTestServletWebApp {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceParticipationGroupeTest.class);

    public ServiceParticipationGroupeTest() {
        super(new ServiceParticipationGroupe());
    }

    @Test
    public void testRejoindreGroupe() throws SQLException {

        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idee.getId(), group.getId());

        int groupSuggestion = GROUP_IDEA_SUGGESTION.with(firefox, idee, group).sendItTo(firefox);
        assertNotifDoesExists(groupSuggestion);

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        when(request.getParameter("amount")).thenReturn(32 + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(groupSuggestion);
        IdeesRepository.remove(idee);
    }

    @Test
    public void testRejoindrePuisAnnuler() throws SQLException {

        // On crée un groupe sur une idée
        logger.info("[Perf] Démarrage...");
        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        logger.info("[Perf] Idée créée ! Création du groupe...");
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        logger.info("[Perf] Groupe créé ! éservation de l'idée par le groupe...");
        IdeesRepository.bookByGroup(idee.getId(), group.getId());
        logger.info("[Perf] OK! Vérication qu'il n'existe pas de notifications...");
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(moiAutre)
                                           .whereIdea(idee)
                                           .hasAny());

        // -----------------------
        // Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        when(request.getParameter("amount")).thenReturn(String.valueOf(32));
        logger.info("[Perf] OK! Envoie de la requête post...");
        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        logger.info("[Perf] OK! Vérification des notifications...");
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.JOIN_GROUP)
                                            .whereIdea(idee)
                                            .fetch().size());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(moiAutre)
                                           .whereType(NType.LEAVE_GROUP)
                                           .whereIdea(idee)
                                           .hasAny());
        logger.info("[Perf] OK! Suppression des notifications...");
        NotificationsRepository.terminator().whereOwner(moiAutre).whereType(NType.JOIN_GROUP).terminates();

        // Annulation de la participation
        ServiceAnnulationGroupeTest annulationService = new ServiceAnnulationGroupeTest();
        annulationService.registerParameter(GroupIdeaDetails.GROUP_ID_PARAM, group.getId());
        resp = annulationService.doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.LEAVE_GROUP)
                                            .whereIdea(idee)
                                            .fetch().size());

        // -----------------------
        // Finalement - re - Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        when(request.getParameter("amount")).thenReturn(32 + "");
        resp = doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.JOIN_GROUP)
                                            .whereIdea(idee)
                                            .fetch().size());
        Long nId = NotificationsRepository.fetcher()
                                          .whereOwner(moiAutre)
                                          .whereType(NType.JOIN_GROUP)
                                          .whereIdea(idee)
                                          .fetch()
                                          .get(0).id;

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        when(request.getParameter("amount")).thenReturn(35 + "");
        resp = doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.JOIN_GROUP)
                                            .whereIdea(idee)
                                            .fetch().size());
        assertEquals(nId,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.JOIN_GROUP)
                                            .whereIdea(idee)
                                            .fetch()
                                            .get(0).id);

        // Finalement - re - Annulation de la participation
        annulationService.registerParameter(GroupIdeaDetails.GROUP_ID_PARAM, group.getId());
        resp = annulationService.doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(moiAutre)
                                            .whereType(NType.LEAVE_GROUP)
                                            .whereIdea(idee)
                                            .fetch().size());

        // -----------------------
        // Clean up
        IdeesRepository.remove(idee);
        // On conserve le groupe pour l'historique
        assertTrue(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
    }
}