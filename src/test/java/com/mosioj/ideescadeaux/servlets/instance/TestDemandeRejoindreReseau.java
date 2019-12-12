package com.mosioj.ideescadeaux.servlets.instance;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.ideescadeaux.model.repositories.UserRelationRequests;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.ideescadeaux.servlets.service.DemandeRejoindreReseauService;
import com.mosioj.ideescadeaux.utils.RootingsUtils;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;

public class TestDemandeRejoindreReseau extends AbstractTestServlet {

	public TestDemandeRejoindreReseau() {
		super(new DemandeRejoindreReseauService());
	}

	@Before
	public void before() {
		when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
	}

	@Test
	public void testPostEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("user_id"));
		verify(request, atMost(1)).getParameter(anyString());
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException, SQLException {

		UserRelationRequests urr = new UserRelationRequests();
		final int otherUserNotFriendYet = 23;
		urr.cancelRequest(_OWNER_ID_, otherUserNotFriendYet);
		
		int suggestionAndAsk = notif.addNotification(_OWNER_ID_, new NotifNewRelationSuggestion(otherUserNotFriendYet, "Toto"));
		int suggestionAndAsked = notif.addNotification(otherUserNotFriendYet, new NotifNewRelationSuggestion(_OWNER_ID_, "Toto"));
		assertNotifDoesExists(suggestionAndAsk);
		assertNotifDoesExists(suggestionAndAsked);

		// Should not throw an exception
		when(request.getParameter("user_id")).thenReturn("23");
		doTestPost(request, response);

		assertNotifDoesNotExists(suggestionAndAsk);
		assertNotifDoesNotExists(suggestionAndAsked);
	}
	
	@Test
	public void testAlreadySent() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("10");
		
		// Should not throw an exception
		doTestPost(request, response);

		verify(request).setAttribute(eq("error_message"), eq("Vous avez déjà envoyé une demande pour cette personne."));
	}

	@Test
	public void testGroupAlreadyExist() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("user_id")).thenReturn("1");
		
		// Should not throw an exception
		doTestPost(request, response);
		
		verify(request).setAttribute(eq("error_message"), eq("Vous faites déjà parti du même réseau."));
	}

}
