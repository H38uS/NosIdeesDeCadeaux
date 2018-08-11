package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.servlets.controllers.idees.modification.RemoveOneIdea;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestRemoveOneIdea extends AbstractTestServlet {

	public TestRemoveOneIdea() {
		super(new RemoveOneIdea());
	}

	@Test
	public void testDelete() throws SQLException, ServletException, IOException {
		
		int id = idees.addIdea(_OWNER_ID_, "generated", "", 0, null, null);
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(0, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));

		when(request.getParameter(RemoveOneIdea.IDEE_ID_PARAM)).thenReturn(id+"");
		doTestPost(request, response);
		
		assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));
	}
	
	@Test
	public void testDeleteWithGroupBooking() throws SQLException, ServletException, IOException {

		// Creation de l'idée
		int id = idees.addIdea(_OWNER_ID_, "generated", "", 0, null, null);
		assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		
		// Creation du groupe
		GroupIdea g = new GroupIdea();
		int group = g.createAGroup(200, 10, 10);
		idees.bookByGroup(id, group);
		Idee idee = idees.getIdea(id);
		assertEquals(group, idee.getGroupKDO());
		assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
		assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));

		// Suppression
		when(request.getParameter(RemoveOneIdea.IDEE_ID_PARAM)).thenReturn(id+"");
		doTestPost(request, response);
		
		// Validation que cela supprime tout
		assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
		assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
		assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));
	}

}
