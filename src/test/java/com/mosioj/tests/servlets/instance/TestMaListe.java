package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.servlets.controllers.idees.MaListe;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestMaListe extends AbstractTestServlet {

	public TestMaListe() throws SQLException {
		super(new MaListe());
	}

	@Before
	public void before() throws SQLException {
		when(request.getRequestDispatcher(MaListe.VIEW_PAGE_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testGetSuccess() throws ServletException, IOException, SQLException {
		doTestGet(request, response);
		verify(request).getRequestDispatcher(eq(MaListe.VIEW_PAGE_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException, SQLException {
		
		int noIdea = notif.addNotification(_OWNER_ID_, new NotifNoIdea());
		assertNotifDoesExists(noIdea);

		Map<String, String> param = new HashMap<String, String>();
		param.put("text", "Ma super idée wouhouuuu");
		param.put("priority", "1");
		createMultiPartRequest(param);
		doTestPost(request, response);

		verify(request).getRequestDispatcher(eq(MaListe.VIEW_PAGE_URL));
		verify(request, never()).setAttribute(eq("errors"), anyObject());
		assertNotifDoesNotExists(noIdea);
	}
}
