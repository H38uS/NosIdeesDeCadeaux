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

import com.mosioj.servlets.controllers.DemandeRejoindreGroupe;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.RootingsUtils;

public class TestDemandeRejoindreGroupe extends AbstractTestServlet {

	public TestDemandeRejoindreGroupe() {
		super(new DemandeRejoindreGroupe());
	}

	@Before
	public void before() {
		when(request.getRequestDispatcher(DemandeRejoindreGroupe.SUCCESS_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(DemandeRejoindreGroupe.ERROR_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
	}

	@Test
	public void testPostEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("groupe_id"));
		verify(request, atMost(1)).getParameter(anyString());

		// Error in processing
		verify(request).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreGroupe.SUCCESS_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreGroupe.ERROR_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException {

		when(request.getParameter("groupe_id")).thenReturn("1");

		// Should not throw an exception
		doTestPost(request, response);

		verify(request).getRequestDispatcher(eq(DemandeRejoindreGroupe.SUCCESS_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreGroupe.ERROR_URL));
	}
	
	@Test
	public void testAlreadySent() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("groupe_id")).thenReturn("1");
		when(groupeJoinRequest.associationExists(32, 1)).thenReturn(true);
		
		// Should not throw an exception
		doTestPost(request, response);

		verify(request).setAttribute(eq("error_message"), eq("Vous avez déjà envoyé une demande pour ce groupe."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreGroupe.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreGroupe.SUCCESS_URL));
	}

	@Test
	public void testGroupAlreadyExist() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("groupe_id")).thenReturn("1");

		when(groupes.getName(1)).thenReturn("My group name !!!");
		when(groupes.associationExists(1, 32)).thenReturn(true);
		
		// Should not throw an exception
		doTestPost(request, response);
		
		verify(request).setAttribute(eq("error_message"), eq("Vous faites déjà parti de ce groupe."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreGroupe.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreGroupe.SUCCESS_URL));
	}
	
	// TODO : faire un test pour les liens : parser les jsp et vérifier qu'on en a pas des morts
	
	// TODO : faire un test rechercher groupe, et notamment quand on a déjà envoyé une demande

}