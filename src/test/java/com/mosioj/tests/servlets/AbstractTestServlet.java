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

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.Users;
import com.mosioj.notifications.NotificationManager;
import com.mosioj.servlets.IdeesCadeauxServlet;
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
	protected Groupes groupes;
	protected GroupeJoinRequests groupeJoinRequest;
	protected DataSourceIdKDo validator;
	protected Idees idees;
	protected Categories cat;
	protected Priorites prio;
	protected NotificationManager notif;
	
	protected final IdeesCadeauxServlet instance;
	
	public AbstractTestServlet(IdeesCadeauxServlet pInstance) {
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		idees = mock(Idees.class);
		cat = mock(Categories.class);
		prio = mock(Priorites.class);
		notif = mock(NotificationManager.class);
		
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute("userid")).thenReturn(_OWNER_ID_);
		
		groupes = mock(Groupes.class);
		groupeJoinRequest = mock(GroupeJoinRequests.class);
		validator = mock(DataSourceIdKDo.class);
		users = mock(Users.class);
		instance = pInstance;
		instance.setUsers(users);
		instance.setGroupes(groupes);
		instance.setGroupeJoinRequests(groupeJoinRequest);
		instance.setValidatorConnection(validator);
		instance.setIdees(idees);
		instance.setCat(cat);
		instance.setPrio(prio);
		instance.setNotificationManager(notif);
		
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
			File web = new File(root, "WebContent");
			
			assertTrue(web.exists());			
			File file = new File(web, path);
			assertTrue("La jsp " + file + " n'existe pas.", file.exists());
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
