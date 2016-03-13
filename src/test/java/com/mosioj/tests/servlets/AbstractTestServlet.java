package com.mosioj.tests.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.powermock.core.classloader.annotations.PowerMockIgnore;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.tests.TemplateTest;

@PowerMockIgnore( {"javax.management.*"}) 
public abstract class AbstractTestServlet extends TemplateTest {

	protected RequestDispatcher dispatcher;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	
	protected final IdeesCadeauxServlet instance;
	
	// TODO tester l'instanciation sans argument de toutes les servlets
	
	public AbstractTestServlet(IdeesCadeauxServlet pInstance) {
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("userid")).thenReturn(32);
		
		instance = pInstance;
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
