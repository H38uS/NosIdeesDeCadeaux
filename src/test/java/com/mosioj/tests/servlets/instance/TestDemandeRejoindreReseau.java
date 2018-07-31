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

import com.mosioj.model.table.UserRelationRequests;
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

		UserRelationRequests urr = new UserRelationRequests();
		urr.cancelRequest(26, 23);
		when(request.getParameter("user_id")).thenReturn("23");

		// Should not throw an exception
		doTestPost(request, response);

		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
	}
	
	@Test
	public void testAlreadySent() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("10");
		
		// Should not throw an exception
		doTestPost(request, response);

		verify(request).setAttribute(eq("error_message"), eq("Vous avez déjà envoyé une demande pour cette personne."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
	}

	@Test
	public void testGroupAlreadyExist() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("1");
		
		// Should not throw an exception
		doTestPost(request, response);
		
		verify(request).setAttribute(eq("error_message"), eq("Vous faites déjà parti du même réseau."));
		verify(request).getRequestDispatcher(eq(DemandeRejoindreReseau.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(DemandeRejoindreReseau.SUCCESS_URL));
	}

}
