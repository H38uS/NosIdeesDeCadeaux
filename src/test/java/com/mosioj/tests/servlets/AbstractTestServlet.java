package com.mosioj.tests.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.UserParameters;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.compte.CreationCompte;
import com.mosioj.tests.TemplateTest;
import com.mosioj.utils.database.DataSourceIdKDo;

public abstract class AbstractTestServlet extends TemplateTest {

	protected static final int _OWNER_ID_ = 32;
	
	protected RequestDispatcher dispatcher;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	
	// Tables
	protected DataSourceIdKDo db;
	protected Users users;
	protected UserRelations userRelations;
	protected UserRelationRequests userRelationRequests;
	protected DataSourceIdKDo validator;
	protected Idees idees;
	protected Categories cat;
	protected Priorites prio;
	protected Notifications notif;
	protected UserParameters userParameters;
	
	protected final IdeesCadeauxServlet instance;
	
	public AbstractTestServlet(IdeesCadeauxServlet pInstance) {
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		idees = mock(Idees.class);
		cat = mock(Categories.class);
		prio = mock(Priorites.class);
		notif = mock(Notifications.class);
		userParameters = mock(UserParameters.class);
		
		when(request.getSession()).thenReturn(session);
		when(request.getRequestURL()).thenReturn(new StringBuffer(CreationCompte.HTTP_LOCALHOST_8080));
		when(request.getContextPath()).thenReturn("");
		when(session.getAttribute("userid")).thenReturn(_OWNER_ID_);
		when(session.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		
		userRelations = mock(UserRelations.class);
		userRelationRequests = mock(UserRelationRequests.class);
		validator = mock(DataSourceIdKDo.class);
		users = mock(Users.class);
		instance = pInstance;
		instance.setUsers(users);
		instance.setUserRelations(userRelations);
		instance.setUserRelationRequests(userRelationRequests);
		instance.setIdees(idees);
		instance.setCat(cat);
		instance.setPrio(prio);
		instance.setNotificationManager(notif);
		instance.setUserParameters(userParameters);
		
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
		for (Field field : fields) {
			
			String name = field.getName();
			if (!name.contains("URL")) {
				continue;
			}
			
			String path = (String) field.get(null);
			File web = new File(root, "WebContent");
			
			assertTrue(web.exists());			
			File file = new File(web, path);
			assertTrue("La jsp " + file + " n'existe pas.", file.exists());
		}
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
