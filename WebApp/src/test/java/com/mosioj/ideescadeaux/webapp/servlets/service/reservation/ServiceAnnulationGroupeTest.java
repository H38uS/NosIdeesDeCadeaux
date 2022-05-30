package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.GROUP_IDEA_SUGGESTION;
import static com.mosioj.ideescadeaux.core.model.notifications.NType.LEAVE_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceAnnulationGroupeTest extends AbstractTestServletWebApp {

    public ServiceAnnulationGroupeTest() {
        super(new ServiceAnnulationGroupe());
    }

    @Test
    public void testAnnulerParticipation() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("toto")
                                                    .withPriority(p));
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, firefox);
        GroupIdeaContentRepository.addNewAmount(group, moiAutre, 25);
        IdeesRepository.bookByGroup(idee, group);

        assertEquals(2,
                     (int) GroupIdeaRepository.getGroupDetails(group.getId())
                                              .map(IdeaGroup::getShares)
                                              .map(List::size)
                                              .orElse(-1));

        // Les notifs à vérifier
        // _MOI_AUTRE_ déclenche la requête d'annulation
        // On nous avait suggéré le groupe => la notif disparait
        int groupSuggestion = GROUP_IDEA_SUGGESTION.with(friendOfFirefox, idee, group).sendItTo(moiAutre);
        assertNotifDoesExists(groupSuggestion);

        // On avait notifié un amis quand on a rejoint le groupe => ça disparait
        int geShouldDisapear1 = NType.JOIN_GROUP.with(moiAutre, idee, group).sendItTo(friendOfFirefox);
        assertNotifDoesExists(geShouldDisapear1);

        // On nous avait notifié => ça disparait
        int geShouldDisapear2 = NType.JOIN_GROUP.with(friendOfFirefox, idee, group).sendItTo(moiAutre);
        assertNotifDoesExists(geShouldDisapear2);

        // D'autres personnes se sont notifié entre eux => ils s'en foutent qu'on partent : ça reste
        int groupEvolutionShouldStay = NType.JOIN_GROUP.with(theAdmin, idee, group).sendItTo(friendOfFirefox);
        assertNotifDoesExists(groupEvolutionShouldStay);

        // Annulation de la participation de _OWNER_ID_ aka friendOfFirefox
        when(session.getAttribute("connected_user")).thenReturn(moiAutre);
        when(request.getParameter(ServiceAnnulationGroupe.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(geShouldDisapear1);
        assertNotifDoesNotExists(geShouldDisapear2);
        assertNotifDoesNotExists(groupSuggestion);
        assertNotifDoesExists(groupEvolutionShouldStay);

        IdeesRepository.remove(idee);
        // On conserve le groupe pour l'historique
        assertTrue(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
    }

    @Test
    public void testNoNotificationsAreSentToOurselfWhenWeLeaveTheGroup() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("toto")
                                                    .withPriority(p));
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, firefox);
        GroupIdeaContentRepository.addNewAmount(group, moiAutre, 25);
        IdeesRepository.bookByGroup(idee, group);

        // Quand on annule notre participation
        when(session.getAttribute("connected_user")).thenReturn(moiAutre);
        when(request.getParameter(ServiceAnnulationGroupe.GROUP_ID_PARAM)).thenReturn(String.valueOf(group.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Alors uniquement firefox reçoit une notification
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.findNotificationsMatching(LEAVE_GROUP.with(moiAutre, idee, group)
                                                                                  .setOwner(firefox)).size());
        assertEquals(0,
                     NotificationsRepository.findNotificationsMatching(LEAVE_GROUP.with(moiAutre, idee, group)
                                                                                  .setOwner(moiAutre)).size());

        // Nettoyage
        GroupIdeaContentRepository.removeParticipationOfTo(group, moiAutre);
        GroupIdeaRepository.deleteGroup(group);
        IdeesRepository.trueRemove(idee);
    }
}