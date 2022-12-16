package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaContentRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaRepository;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Set;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.GROUP_IDEA_SUGGESTION;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.LEAVE_GROUP;
import static org.junit.Assert.*;

public class ServiceAnnulationGroupeTest extends AbstractTestServletWebApp {

    public ServiceAnnulationGroupeTest() {
        super(new ServiceAnnulationGroupe());
    }

    @Test
    public void testAnnulerAvecUnSeulMembre() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("toto")
                                                    .withPriority(p));
        IdeaGroup group = GroupIdeaRepository.createAGroup(300, 250, firefox);
        IdeesRepository.bookByGroup(idee, group);
        assertEquals(1,
                     (int) GroupIdeaRepository.getGroupDetails(group.getId())
                                              .map(IdeaGroup::getShares)
                                              .map(Set::size)
                                              .orElse(-1));

        // Annulation de la participation de firefox
        bindPostRequestParam(ServiceAnnulationGroupe.GROUP_ID_PARAM, String.valueOf(group.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Le groupe doit avoir été supprimé, ainsi que sa référence dans l'idée
        assertTrue(resp.isOK());
        assertTrue(IdeesRepository.canBook(IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new),
                                           firefox));
        assertFalse(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
        IdeesRepository.trueRemove(idee);
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
                                              .map(Set::size)
                                              .orElse(-1));

        // Les notifs à vérifier
        // _MOI_AUTRE_ déclenche la requête d'annulation
        // On nous avait suggéré le groupe => la notif disparait
        Notification groupSuggestion = GROUP_IDEA_SUGGESTION.with(friendOfFirefox, idee, group).sendItTo(moiAutre);
        assertNotifDoesExists(groupSuggestion);

        // On avait notifié un amis quand on a rejoint le groupe => ça disparait
        Notification geShouldDisapear1 = NType.JOIN_GROUP.with(moiAutre, idee, group).sendItTo(friendOfFirefox);
        assertNotifDoesExists(geShouldDisapear1);

        // On nous avait notifié => ça disparait
        Notification geShouldDisapear2 = NType.JOIN_GROUP.with(friendOfFirefox, idee, group).sendItTo(moiAutre);
        assertNotifDoesExists(geShouldDisapear2);

        // D'autres personnes se sont notifié entre eux => ils s'en foutent qu'on partent : ça reste
        Notification groupEvolutionShouldStay = NType.JOIN_GROUP.with(theAdmin, idee, group).sendItTo(friendOfFirefox);
        assertNotifDoesExists(groupEvolutionShouldStay);

        // Annulation de la participation de _OWNER_ID_ aka friendOfFirefox
        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        bindPostRequestParam(ServiceAnnulationGroupe.GROUP_ID_PARAM, String.valueOf(group.getId()));
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
        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        bindPostRequestParam(ServiceAnnulationGroupe.GROUP_ID_PARAM, String.valueOf(group.getId()));
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