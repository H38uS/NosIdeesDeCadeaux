package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
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

import com.mosioj.ideescadeaux.notifications.instance.NotifAdministration;
import com.mosioj.ideescadeaux.servlets.controllers.compte.CreationCompte;
import com.mosioj.ideescadeaux.utils.RootingsUtils;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestCreationCompte extends AbstractTestServlet {

	public TestCreationCompte() {
		super(new CreationCompte());
	}

	@Before
	public void before() {

		when(request.getRequestDispatcher(CreationCompte.FORM_URL)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
		when(request.getRequestDispatcher(CreationCompte.SUCCES_URL)).thenReturn(dispatcher);
	}

	@Test
	public void testEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("email"));
		verify(request).getParameter(eq("pwd"));
		verify(request, atMost(4)).getParameter(anyString());

		// Parameters were invalid
		verify(request).setAttribute(eq("email_errors"), anyObject());
		verify(request).setAttribute(eq("pwd_errors"), anyObject());

		// Error in processing
		verify(request).getRequestDispatcher(eq(CreationCompte.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(CreationCompte.SUCCES_URL));
	}

	@Test
	public void testSuccess() throws ServletException, IOException, SQLException {

		long count = countNewInscriptionNotification();
		ds.executeUpdate("delete from USERS where email = ?", "tartenpiontoto@hotmaildzndqudn.fr");
		assertEquals(0, ds.selectCountStar("select count(*) from USERS where email = ?", "tartenpiontoto@hotmaildzndqudn.fr"));
		when(request.getParameter("email")).thenReturn("tartenpiontoto@hotmaildzndqudn.fr");
		when(request.getParameter("pwd")).thenReturn("mydummypwd");

		// Should not throw an exception
		doTestPost(request, response);

		// Success
		assertEquals(1, ds.selectCountStar("select count(*) from USERS where email = ?", "tartenpiontoto@hotmaildzndqudn.fr"));
		verify(request).getRequestDispatcher(eq(CreationCompte.SUCCES_URL));
		verify(request, never()).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(CreationCompte.FORM_URL));
		assertEquals(count + 1, countNewInscriptionNotification());
	}

	protected long countNewInscriptionNotification() throws SQLException {
		return notif.getUserNotifications(_ADMIN_ID_)
					.stream()
					.filter(n -> NotifAdministration.class.equals(n.getClass()))
					.count();
	}

}
