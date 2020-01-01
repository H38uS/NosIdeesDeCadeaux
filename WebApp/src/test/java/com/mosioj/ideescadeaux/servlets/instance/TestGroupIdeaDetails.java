package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.model.repositories.columns.GroupIdeaColumns;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifGroupEvolution;
import com.mosioj.ideescadeaux.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.idees.reservation.GroupIdeaDetails;
import com.mosioj.ideescadeaux.model.database.NoRowsException;
import org.junit.Test;

import java.sql.SQLException;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestGroupIdeaDetails extends AbstractTestServlet {

    public TestGroupIdeaDetails() {
        super(new GroupIdeaDetails());
    }

    @Test
    public void testGet() throws SQLException {

        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idea, id);

        int groupSuggestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                      new NotifGroupSuggestion(firefox,
                                                                                     id,
                                                                                     IdeesRepository.getIdeaWithoutEnrichment(idea)));
        assertNotifDoesExists(groupSuggestion);

        when(request.getRequestDispatcher(GroupIdeaDetails.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        doTestGet();
        assertNotifDoesNotExists(groupSuggestion);

        IdeesRepository.remove(idea);
    }

    @Test
    public void testRejoindreGroupe() throws SQLException {

        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idea, id);

        int groupSuggestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                      new NotifGroupSuggestion(firefox,
                                                                                     id,
                                                                                     IdeesRepository.getIdeaWithoutEnrichment(idea)));
        assertNotifDoesExists(groupSuggestion);

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        doTestPost();

        assertNotifDoesNotExists(groupSuggestion);
        IdeesRepository.remove(idea);
    }

    @Test
    public void testAnnulerParticipation() throws SQLException, NoRowsException {

        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _OWNER_ID_);
        GroupIdeaRepository.addNewAmount(25, moiAutre.id, id);
        IdeesRepository.bookByGroup(idea, id);
        assertGroupExists(id);

        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(idea);
        int groupSuggestion = NotificationsRepository.addNotification(_MOI_AUTRE_, new NotifGroupSuggestion(moiAutre, id, idee));
        int groupEvolutionShouldDisapear = NotificationsRepository.addNotification(_MOI_AUTRE_,
                                                                                   new NotifGroupEvolution(firefox, // == _OWNER_ID_
                                                                                         id,
                                                                                         idee,
                                                                                         true));
        int groupEvolutionShouldStay = NotificationsRepository.addNotification(_MOI_AUTRE_,
                                                                               new NotifGroupEvolution(friendOfFirefox, id, idee, true));
        assertNotifDoesExists(groupSuggestion);
        assertNotifDoesExists(groupEvolutionShouldDisapear);
        assertNotifDoesExists(groupEvolutionShouldStay);

        // Annulation de la participation de _OWNER_ID_ aka friendOfFirefox
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn("annulation");
        doTestPost();

        assertNotifDoesNotExists(groupEvolutionShouldDisapear);
        assertNotifDoesNotExists(groupSuggestion);
        assertNotifDoesExists(groupEvolutionShouldStay);

        IdeesRepository.remove(idea);
        assertEquals(0,
                     ds.selectInt(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                       GroupIdeaRepository.TABLE_NAME,
                                                       GroupIdeaColumns.ID),
                                  id));
    }

    @Test
    public void testRejoindrePuisAnnuler() throws SQLException, NoRowsException {

        // On crée un groupe sur une idée
        int idea = IdeesRepository.addIdea(friendOfFirefox, "toto", null, 0, null, null, null);
        int id = GroupIdeaRepository.createAGroup(300, 250, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idea, id);
        assertGroupExists(id);
        assertEquals(0,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());

        // -----------------------
        // Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());
        NotificationsRepository.removeAllType(moiAutre, NotificationType.GROUP_EVOLUTION);

        // Annulation de la participation
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn("annulation");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());

        // -----------------------
        // Finalement - re - Participation au groupe
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(32 + "");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());
        int nId = NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                         .get(0).id;

        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn(35 + "");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());
        assertEquals(nId,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .get(0).id);

        // Finalement - re - Annulation de la participation
        when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
        when(request.getParameter("amount")).thenReturn("annulation");
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea)
                                            .size());
        assertTrue(NotificationsRepository.getNotifications(_MOI_AUTRE_,
                                                            NotificationType.GROUP_EVOLUTION,
                                                            ParameterName.IDEA_ID,
                                                            idea).get(0).text.contains("quitté"));

        // -----------------------
        // Clean up
        IdeesRepository.remove(idea);
        assertEquals(0,
                     ds.selectInt(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                       GroupIdeaRepository.TABLE_NAME,
                                                       GroupIdeaColumns.ID),
                                  id));
    }

    protected void assertGroupExists(int id) throws SQLException, NoRowsException {
        assertEquals(1,
                     ds.selectInt(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                       GroupIdeaRepository.TABLE_NAME,
                                                       GroupIdeaColumns.ID),
                                  id));
    }

}
