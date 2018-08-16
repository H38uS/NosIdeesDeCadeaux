package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.columns.GroupIdeaColumns;
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
		
		int idea = idees.addIdea(_FRIEND_ID_, "toto", null, 0, null, null);
		int id = groupIdea.createAGroup(300, 250, _MOI_AUTRE_);
		idees.bookByGroup(idea, id);

		int groupSuggestion = notif.addNotification(_OWNER_ID_, new NotifGroupSuggestion(firefox, id, idees.getIdea(idea)));
		assertNotifDoesExists(groupSuggestion);
		
		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id+"");
		when(request.getParameter("amount")).thenReturn(32+"");
		doTestPost(request, response);

		assertNotifDoesNotExists(groupSuggestion);
		idees.remove(idea);
	}

	@Test
	public void testAnnulerParticipation() throws SQLException, ServletException, IOException, NoRowsException {

		int idea = idees.addIdea(_FRIEND_ID_, "toto", null, 0, null, null);
		int id = groupIdea.createAGroup(300, 250, _OWNER_ID_);
		idees.bookByGroup(idea, id);
		assertEquals(	1,
		             	ds.selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
		             	             	                     	GroupIdea.TABLE_NAME,
		             	             	                     	GroupIdeaColumns.ID),
		             	             	id));

		int groupSuggestion = notif.addNotification(_MOI_AUTRE_, new NotifGroupSuggestion(moiAutre, id, idees.getIdea(idea)));
		assertNotifDoesExists(groupSuggestion);

		when(request.getParameter(GroupIdeaDetails.GROUP_ID_PARAM)).thenReturn(id+"");
		when(request.getParameter("amount")).thenReturn("annulation");
		doTestPost(request, response);

		assertNotifDoesNotExists(groupSuggestion);
		idees.remove(idea);
		assertEquals(	0,
						ds.selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
																GroupIdea.TABLE_NAME,
																GroupIdeaColumns.ID),
										id));
	}

}
