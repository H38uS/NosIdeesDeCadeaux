package com.mosioj.tests.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

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
		
		try {
			validateInstanceLinks();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Tests that all links for the current tested instance exists.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void validateInstanceLinks() throws IllegalArgumentException, IllegalAccessException {
		
		Field[] fields = instance.getClass().getFields();
		boolean hasAURL = false;
		for (Field field : fields) {
			
			String name = field.getName();
			if (!name.contains("URL")) {
				continue;
			}

			hasAURL = true;
			
			String path = (String) field.get(null);
			File root = new File(getClass().getResource("/").getFile()).getParentFile().getParentFile();
			File web = new File(root, "src/main/webapp");
			
			assertTrue(web.exists());			
			assertTrue(new File(web, path).exists());
		}
		
		assertTrue("No URL static field found - this is really strange !!!", hasAURL);
		
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
