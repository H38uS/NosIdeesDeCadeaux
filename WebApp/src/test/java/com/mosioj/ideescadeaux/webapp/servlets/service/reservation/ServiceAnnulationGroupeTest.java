package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupEvolution;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceAnnulationGroupeTest extends AbstractTestServletWebApp {

    public ServiceAnnulationGroupeTest() {
        super(new ServiceAnnulationGroupe());
    }

    @Test
    public void testAnnulerParticipation() throws SQLException {

        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int group = GroupIdeaRepository.createAGroup(300, 250, _OWNER_ID_);
        GroupIdeaRepository.addNewAmount(25, moiAutre.id, group);
        IdeesRepository.bookByGroup(idea, group);
        assertTrue(GroupIdeaRepository.getGroupDetails(group).isPresent());

        // Les notifs à vérifier
        Idee idee = IdeesRepository.getIdea(idea).orElseThrow(SQLException::new);

        // _MOI_AUTRE_ déclenche la requête d'annulation
        // On nous avait suggéré le groupe => la notif disparait
        final NotifGroupSuggestion suggestionParUnAmi = new NotifGroupSuggestion(friendOfFirefox, group, idee);
        int groupSuggestion = NotificationsRepository.addNotification(_MOI_AUTRE_, suggestionParUnAmi);
        assertNotifDoesExists(groupSuggestion);

        // On avait notifié un amis quand on a rejoint le groupe => ça disparait
        final NotifGroupEvolution notreEvolution = new NotifGroupEvolution(moiAutre, group, idee, true);
        int geShouldDisapear1 = NotificationsRepository.addNotification(_FRIEND_ID_, notreEvolution);
        assertNotifDoesExists(geShouldDisapear1);

        // On nous avait notifié => ça disparait
        final NotifGroupEvolution evolutionAmiANous = new NotifGroupEvolution(friendOfFirefox, group, idee, true);
        int geShouldDisapear2 = NotificationsRepository.addNotification(_MOI_AUTRE_, evolutionAmiANous);
        assertNotifDoesExists(geShouldDisapear2);

        // D'autres personnes se sont notifié entre eux => ils s'en foutent qu'on partent : ça reste
        final NotifGroupEvolution evolutionAutres = new NotifGroupEvolution(theAdmin, group, idee, true);
        int groupEvolutionShouldStay = NotificationsRepository.addNotification(_FRIEND_ID_, evolutionAutres);
        assertNotifDoesExists(groupEvolutionShouldStay);

        // Annulation de la participation de _OWNER_ID_ aka friendOfFirefox
        when(session.getAttribute("connected_user")).thenReturn(moiAutre);
        when(request.getParameter(ServiceAnnulationGroupe.GROUP_ID_PARAM)).thenReturn(group + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(geShouldDisapear1);
        assertNotifDoesNotExists(geShouldDisapear2);
        assertNotifDoesNotExists(groupSuggestion);
        assertNotifDoesExists(groupEvolutionShouldStay);

        IdeesRepository.remove(idea);
        assertFalse(GroupIdeaRepository.getGroupDetails(group).isPresent());
    }
}