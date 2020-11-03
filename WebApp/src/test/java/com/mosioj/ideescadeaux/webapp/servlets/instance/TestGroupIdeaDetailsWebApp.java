package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.columns.GroupIdeaColumns;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.GroupIdeaDetails;
import com.mosioj.ideescadeaux.webapp.servlets.service.reservation.ServiceAnnulationGroupeTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestGroupIdeaDetailsWebApp extends AbstractTestServletWebApp {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(TestGroupIdeaDetailsWebApp.class);

    public TestGroupIdeaDetailsWebApp() {
        super(new GroupIdeaDetails());
    }

    @Test
    public void testGet() throws SQLException {

        int ideaId = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(ideaId, id);

        Idee idea = IdeesRepository.getIdea(ideaId).orElseThrow(SQLException::new);
        NotifGroupSuggestion notifGroupSuggestion = new NotifGroupSuggestion(firefox, id, idea);
        int groupSuggestion = NotificationsRepository.addNotification(_OWNER_ID_, notifGroupSuggestion);
        assertNotifDoesExists(groupSuggestion);

        when(request.getRequestDispatcher(GroupIdeaDetails.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        doTestGet();
        assertNotifDoesNotExists(groupSuggestion);

        IdeesRepository.remove(ideaId);
    }

    @Test
    public void testRejoindreGroupe() throws SQLException {

        int ideaId = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(ideaId, id);

        Idee idea = IdeesRepository.getIdea(ideaId).orElseThrow(SQLException::new);
        int groupSuggestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                      new NotifGroupSuggestion(firefox, id, idea));
        assertNotifDoesExists(groupSuggestion);

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        doTestPost();

        assertNotifDoesNotExists(groupSuggestion);
        IdeesRepository.remove(ideaId);
    }

    @Test
    public void testRejoindrePuisAnnuler() throws SQLException {

        // On crée un groupe sur une idée
        logger.info("[Perf] Démarrage...");
        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        logger.info("[Perf] Idée créée ! Création du groupe...");
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        logger.info("[Perf] Groupe créé ! éservation de l'idée par le groupe...");
        IdeesRepository.bookByGroup(idea, id);
        logger.info("[Perf] OK! Vérication qu'il n'existe pas de notifications...");
        assertTrue(GroupIdeaRepository.getGroupDetails(id).isPresent());
        assertEquals(0,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());

        // -----------------------
        // Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        logger.info("[Perf] OK! Envoie de la requête post...");
        doTestPost();
        logger.info("[Perf] OK! Vérification des notifications...");
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());
        logger.info("[Perf] OK! Suppression des notifications...");
        NotificationsRepository.removeAllType(moiAutre, NotificationType.GROUP_EVOLUTION);

        // Annulation de la participation
        ServiceAnnulationGroupeTest annulationService = new ServiceAnnulationGroupeTest();
        annulationService.registerParameter(GroupIdeaDetails.GROUP_ID_PARAM, id);
        StringServiceResponse resp = annulationService.doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());

        // -----------------------
        // Finalement - re - Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());
        int nId = NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                           NotificationType.GROUP_EVOLUTION,
                                                           ParameterName.IDEA_ID,
                                                           idea)
                                         .get(0).id;

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(35 + "");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());
        assertEquals(nId,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .get(0).id);

        // Finalement - re - Annulation de la participation
        annulationService.registerParameter(GroupIdeaDetails.GROUP_ID_PARAM, id);
        resp = annulationService.doTestServicePost();
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                              NotificationType.GROUP_EVOLUTION,
                                                              ParameterName.IDEA_ID,
                                                              idea)
                                            .size());
        assertTrue(NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                            NotificationType.GROUP_EVOLUTION,
                                                            ParameterName.IDEA_ID,
                                                            idea).get(0).text.contains("quitté"));

        // -----------------------
        // Clean up
        IdeesRepository.remove(idea);
        assertEquals(Optional.of(0),
                     ds.selectInt(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                       GroupIdeaRepository.TABLE_NAME,
                                                       GroupIdeaColumns.ID),
                                  id));
    }

}
