package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AdministrationGroupe;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestAdministrationGroupe extends AbstractTestServlet {

	public TestAdministrationGroupe() throws SQLException {
		super(new AdministrationGroupe());
	}

	@Before
	public void before() throws SQLException {
		when(request.getRequestDispatcher(AdministrationGroupe.ERROR_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(AdministrationGroupe.FORM_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testPostEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Parameters were invalid
		verify(request).setAttribute(eq("error_message"), eq("Le groupe fourni n'existe pas."));

		// Error in processing
		verify(request).getRequestDispatcher(eq(AdministrationGroupe.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(AdministrationGroupe.FORM_URL));
	}

	@Test
	public void testInvalidRights() throws ServletException, IOException {
		
		when(request.getParameter("groupId")).thenReturn("18");
		
		// Should not throw an exception
		doTestPost(request, response);
		
		// Parameters were invalid
		verify(request).setAttribute(eq("error_message"), eq("Vous ne pouvez administrer que votre groupe."));
		
		// Error in processing
		verify(request).getRequestDispatcher(eq(AdministrationGroupe.ERROR_URL));
		verify(request, never()).getRequestDispatcher(eq(AdministrationGroupe.FORM_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException, SQLException {

		when(request.getParameter("groupId")).thenReturn("18");
		when(groupes.isGroupOwner(32, 18)).thenReturn(true);
		
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("choix_7", new String[]{"Accepter"});
		params.put("choix_8", new String[]{"Refuser"});
		params.put("choix_9", new String[]{"Accepter"});
		params.put("choix_123242", new String[]{"Refuser"});
		params.put("choix_79", new String[]{"Refuser"});
		when(request.getParameterMap()).thenReturn(params);

		// Should not throw an exception
		doTestPost(request, response);

		// Success
		verify(groupes, times(2)).addAssociation(eq(18), anyInt());
		verify(groupeJoinRequest, times(3)).cancelRequest(eq(18), anyInt());
		
		verify(request, never()).setAttribute(eq("error_message"), anyObject());
		verify(request).getRequestDispatcher(eq(AdministrationGroupe.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(AdministrationGroupe.ERROR_URL));
	}

	@Test
	public void testGetSuccess() throws ServletException, IOException, SQLException {
		
		when(groupes.getOwnerGroupId(32)).thenReturn(3);
		when(groupes.getUsers(3)).thenReturn(new ArrayList<User>());

		// Should not throw an exception
		doTestGet(request, response);
		
		verify(request).setAttribute(eq("members"), anyObject());
		verify(request).setAttribute(eq("groupId"), anyObject());
		verify(request).setAttribute(eq("demandes"), anyObject());
		
		verify(request).getRequestDispatcher(eq(AdministrationGroupe.FORM_URL));
	}

}
