package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.model.Groupe;
import com.mosioj.servlets.controllers.RechercherGroupe;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestRechercheGroup extends AbstractTestServlet {

	public TestRechercheGroup() {
		super(new RechercherGroupe());
	}
	

	@Before
	public void before() {
		when(request.getRequestDispatcher(RechercherGroupe.FORM_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testPost() throws ServletException, IOException, SQLException {

		List<Groupe> groups = new ArrayList<Groupe>();
		
		when(request.getParameter("name")).thenReturn("monGroupe");
		when(groupes.getGroupsToJoin("monGroupe", _OWNER_ID_)).thenReturn(groups);
		instance.doPost(request, response);

		verify(request).getRequestDispatcher(eq(RechercherGroupe.FORM_URL));
	}

}
