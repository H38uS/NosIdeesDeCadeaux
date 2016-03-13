package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyObject;
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
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mosioj.model.table.Groupes;
import com.mosioj.servlets.controllers.CreationGroupe;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.InternalConnection;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InternalConnection.class)
public class TestCreationGroupe extends AbstractTestServlet {

	public TestCreationGroupe() {
		super(new CreationGroupe());
	}

	@Before
	public void before() {

		when(request.getRequestDispatcher(CreationGroupe.SUCCESS_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(CreationGroupe.EXISTS_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(CreationGroupe.FORM_URL)).thenReturn(dispatcher);

		PowerMockito.mockStatic(InternalConnection.class);
	}

	@Test
	public void testPostEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("name"));
		verify(request, atMost(1)).getParameter(anyString());

		// Parameters were invalid
		verify(request).setAttribute(eq("name_errors"), anyObject());
		verify(request, atMost(2)).setAttribute(anyString(), anyObject());

		// Error in processing
		verify(request).getRequestDispatcher(eq(CreationGroupe.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.EXISTS_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.SUCCESS_URL));
	}

	@Test
	public void testPostSuccess() throws ServletException, IOException {

		when(request.getParameter("name")).thenReturn("Toto à la plage");

		// Should not throw an exception
		doTestPost(request, response);

		// Success
		verify(request).getRequestDispatcher(eq(CreationGroupe.SUCCESS_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.EXISTS_URL));
	}

	@Test
	public void testPostAlreadyExist() throws ServletException, IOException, SQLException {

		when(request.getParameter("name")).thenReturn("Toto à la plage");
		when(InternalConnection.selectInt("select count(*) from " + Groupes.TABLE_NAME + " where owner_id = ?", 32)).thenReturn(1);

		// Should not throw an exception
		doTestPost(request, response);

		verify(request).getRequestDispatcher(eq(CreationGroupe.EXISTS_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(CreationGroupe.SUCCESS_URL));
	}

	@Test
	public void testGetAlreadyExist() throws ServletException, IOException, SQLException {

		when(request.getParameter("name")).thenReturn("Toto à la plage");
		when(InternalConnection.selectInt("select count(*) from " + Groupes.TABLE_NAME + " where owner_id = ?", 32)).thenReturn(1);

		// Should not throw an exception
		doTestGet(request, response);

		verify(request).getRequestDispatcher(eq(CreationGroupe.EXISTS_URL));
	}

	@Test
	public void testGetSuccess() throws ServletException, IOException, SQLException {

		when(request.getParameter("name")).thenReturn("Toto à la plage");

		// Should not throw an exception
		doTestGet(request, response);

		verify(request).getRequestDispatcher(eq(CreationGroupe.FORM_URL));
	}

}
