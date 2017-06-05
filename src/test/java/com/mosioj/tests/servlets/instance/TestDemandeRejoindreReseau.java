package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.relations.DemandeRejoindreReseau;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.RootingsUtils;

public class TestDemandeRejoindreReseau extends AbstractTestServlet {

	public TestDemandeRejoindreReseau() {
		super(new DemandeRejoindreReseau());
	}

	@Before
	public void before() {
		when(request.getRequestDispatcher(DemandeRejoindreReseau.SUCCESS_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(DemandeRejoindreReseau.ERROR_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
	}

	@Test
	public void testPostEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("user_id"));
		verify(request, atMost(1)).getParameter(anyString());

		// Error in processing
		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException, SQLException {

		when(request.getParameter("user_id")).thenReturn("1");
		when(users.getUser(1)).thenReturn(new User(1, "toto", "toto@hotmail.fr"));

		// Should not throw an exception
		doTestPost(request, response);

		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
	}
	
	@Test
	public void testAlreadySent() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("1");
		when(users.getUser(1)).thenReturn(new User(1, "toto", "toto@hotmail.fr"));
		when(userRelationRequests.associationExists(32, 1)).thenReturn(true);
		
		// Should not throw an exception
		doTestPost(request, response);

		verify(request).setAttribute(eq("error_message"), eq("Vous avez déjà envoyé une demande pour cette personne."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
	}

	@Test
	public void testGroupAlreadyExist() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("1");
		when(users.getUser(1)).thenReturn(new User(1, "toto", "toto@hotmail.fr"));

		when(userRelations.associationExists(1, 32)).thenReturn(true);
		
		// Should not throw an exception
		doTestPost(request, response);
		
		verify(request).setAttribute(eq("error_message"), eq("Vous faites déjà parti du même réseau."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
	}
	
	// FIXME : 1 faire un test pour les liens : parser les jsp et vérifier qu'on en a pas des morts

}
