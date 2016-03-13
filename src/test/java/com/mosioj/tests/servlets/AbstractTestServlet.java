package com.mosioj.tests.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.tests.TemplateTest;
import com.mosioj.utils.database.ConnectionIdKDo;

public abstract class AbstractTestServlet extends TemplateTest {

	protected RequestDispatcher dispatcher;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	
	// Tables
	protected ConnectionIdKDo db;
	protected Users users;
	protected Groupes groupes;
	protected GroupeJoinRequests groupeJoinRequest;
	protected ConnectionIdKDo validator;
	
	protected final IdeesCadeauxServlet instance;
	
	public AbstractTestServlet(IdeesCadeauxServlet pInstance) {
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		
		
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("userid")).thenReturn(32);
		
		groupes = mock(Groupes.class);
		groupeJoinRequest = mock(GroupeJoinRequests.class);
		validator = mock(ConnectionIdKDo.class);
		users = mock(Users.class);
		instance = pInstance;
		instance.setUsers(users);
		instance.setGroupes(groupes);
		instance.setGroupeJoinRequests(groupeJoinRequest);
		instance.setValidatorConnection(validator);
		
	}

	/**
	 * Performs a post to the test object.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doTestPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		instance.doPost(request, response);
	}
	
	/**
	 * Performs a get to the test object.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doTestGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		instance.doGet(req, resp);
	}

}
