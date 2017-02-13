package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.idees.MaListe;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestIdees extends AbstractTestServlet {

	public TestIdees() throws SQLException {
		super(new MaListe());
	}

	@Before
	public void before() throws SQLException {
		when(request.getRequestDispatcher(MaListe.VIEW_PAGE_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testGetSuccess() throws ServletException, IOException, SQLException {

		List<Idee> ideas = new ArrayList<Idee>();
		when(idees.getOwnerIdeas(_OWNER_ID_)).thenReturn(ideas);

		doTestGet(request, response);

		verify(request).getRequestDispatcher(eq(MaListe.VIEW_PAGE_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException {

		when(request.getParameter("text")).thenReturn("Ma super idée wouhouuuu");
		when(request.getParameter("priority")).thenReturn("1");
		doTestPost(request, response);
		
		verify(response).sendRedirect(eq(MaListe.PROTECTED_MA_LISTE));
		verify(request, never()).setAttribute(eq("errors"), anyObject());
	}

}
