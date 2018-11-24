package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.columns.GroupIdeaColumns;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifGroupEvolution;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.servlets.controllers.idees.reservation.GroupIdeaDetails;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.NoRowsException;

public class TestGroupIdeaDetails extends AbstractTestServlet {

	public TestGroupIdeaDetails() {
		super(new GroupIdeaDetails());
	}

	@Test
	public void testRejoindreGroupe() throws SQLException, ServletException, IOException {

		int idea = idees.addIdea(_FRIEND_ID_, "toto", null, 0, null, null, null);
		int id = groupIdea.createAGroup(300, 250, _MOI_AUTRE_);
		idees.bookByGroup(idea, id);

		int groupSuggestion = notif.addNotification(_OWNER_ID_,
													new NotifGroupSuggestion(firefox, id, idees.getIdeaWithoutEnrichment(idea)));
		assertNotifDoesExists(groupSuggestion);

		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn(32 + "");
		doTestPost(request, response);

		assertNotifDoesNotExists(groupSuggestion);
		idees.remove(idea);
	}

	@Test
	public void testAnnulerParticipation() throws SQLException, ServletException, IOException, NoRowsException {

		int idea = idees.addIdea(_FRIEND_ID_, "toto", null, 0, null, null, null);
		int id = groupIdea.createAGroup(300, 250, _OWNER_ID_);
		groupIdea.updateAmount(id, _MOI_AUTRE_, 25);
		idees.bookByGroup(idea, id);
		assertGroupExists(id);

		Idee idee = idees.getIdeaWithoutEnrichment(idea);
		int groupSuggestion = notif.addNotification(_MOI_AUTRE_, new NotifGroupSuggestion(moiAutre, id, idee));
		int groupEvolutionShouldDisapear = notif.addNotification(_MOI_AUTRE_, new NotifGroupEvolution(friendOfFirefox, // == _OWNER_ID_
																						id,
																						idee,
																						true));
		int groupEvolutionShouldStay = notif.addNotification(_MOI_AUTRE_, new NotifGroupEvolution(firefox, id, idee, true));
		assertNotifDoesExists(groupSuggestion);
		assertNotifDoesExists(groupEvolutionShouldDisapear);
		assertNotifDoesExists(groupEvolutionShouldStay);

		// Annulation de la participation de _OWNER_ID_ aka friendOfFirefox
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn("annulation");
		doTestPost(request, response);

		assertNotifDoesNotExists(groupEvolutionShouldDisapear);
		assertNotifDoesNotExists(groupSuggestion);
		assertNotifDoesExists(groupEvolutionShouldStay);

		idees.remove(idea);
		assertEquals(	0,
						ds.selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
																GroupIdea.TABLE_NAME,
																GroupIdeaColumns.ID),
										id));
	}

	@Test
	public void testRejoindrePuisAnnuler() throws SQLException, ServletException, IOException, NoRowsException {

		// On crée un groupe sur une idée
		int idea = idees.addIdea(_FRIEND_ID_, "toto", null, 0, null, null, null);
		int id = groupIdea.createAGroup(300, 250, _MOI_AUTRE_);
		idees.bookByGroup(idea, id);
		assertGroupExists(id);
		assertEquals(0, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());

		// -----------------------
		// Participation au groupe
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn(32 + "");
		doTestPost(request, response);
		assertEquals(1, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());
		notif.removeAllType(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION);

		// Annulation de la participation
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn("annulation");
		doTestPost(request, response);
		assertEquals(1, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());

		// -----------------------
		// Finalement - re - Participation au groupe
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn(32 + "");
		doTestPost(request, response);
		assertEquals(1, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());
		int nId = notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).get(0).id;

		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn(35 + "");
		doTestPost(request, response);
		assertEquals(1, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());
		assertEquals(	nId,
						notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).get(0).id);

		// Finalement - re - Annulation de la participation
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id + "");
		when(request.getParameter("amount")).thenReturn("annulation");
		doTestPost(request, response);
		assertEquals(1, notif.getNotifications(_MOI_AUTRE_, NotificationType.GROUP_EVOLUTION, ParameterName.IDEA_ID, idea).size());
		assertTrue(notif.getNotifications(	_MOI_AUTRE_,
											NotificationType.GROUP_EVOLUTION,
											ParameterName.IDEA_ID,
											idea).get(0).text.contains("quitté"));

		// -----------------------
		// Clean up
		idees.remove(idea);
		assertEquals(	0,
						ds.selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
																GroupIdea.TABLE_NAME,
																GroupIdeaColumns.ID),
										id));
	}

	protected void assertGroupExists(int id) throws SQLException, NoRowsException {
		assertEquals(	1,
						ds.selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
																GroupIdea.TABLE_NAME,
																GroupIdeaColumns.ID),
										id));
	}

}
