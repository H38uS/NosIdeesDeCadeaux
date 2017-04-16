package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.servlets.controllers.relations.RechercherPersonne;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestRecherchePersonne extends AbstractTestServlet {

	public TestRecherchePersonne() {
		super(new RechercherPersonne());
	}
	

	@Before
	public void before() {
		when(request.getRequestDispatcher(RechercherPersonne.FORM_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testPost() throws ServletException, IOException, SQLException {
		when(session.getAttribute("userid")).thenReturn(-1);
		when(request.getParameter("name")).thenReturn("monGroupe");
		instance.doPost(request, response);
		verify(request).getRequestDispatcher(eq(RechercherPersonne.FORM_URL));
	}

}