package com.mosioj.controllers.tests;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mosioj.controllers.CreationCompte;
import com.mosioj.utils.database.InternalConnection;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InternalConnection.class)
public class TestCreationCompte {

	private RequestDispatcherForTest dispatcher;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private TestServlet servelet;

	@Before
	public void before() {
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
		dispatcher = Mockito.mock(RequestDispatcherForTest.class);
		when(request.getRequestDispatcher("/public/creation_compte.jsp")).thenReturn(dispatcher);
		when(request.getRequestDispatcher("/public/server_error.jsp")).thenReturn(dispatcher);
		when(request.getRequestDispatcher("/public/succes_creation.jsp")).thenReturn(dispatcher);
		PowerMockito.mockStatic(InternalConnection.class);
		servelet = new TestServlet();
	}

	@Test
	public void testEmptyParameters() throws ServletException, IOException {

		// Should not throw an exception
		servelet.doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("email"));
		verify(request).getParameter(eq("pwd"));
		verify(request, atMost(2)).getParameter(anyString());

		// Parameters were invalid 
		verify(request).setAttribute(eq("email_errors"), anyObject());
		verify(request).setAttribute(eq("pwd_errors"), anyObject());
		verify(request, atMost(2)).setAttribute(anyString(), anyObject());
		
		// Error in processing
		verify(request).getRequestDispatcher(eq("/public/creation_compte.jsp"));
		verify(request, never()).getRequestDispatcher(eq("/public/server_error.jsp"));
		verify(request, never()).getRequestDispatcher(eq("/public/succes_creation.jsp"));
	}
	
	@Test
	public void testSucces() throws ServletException, IOException {

		when(request.getParameter("email")).thenReturn("jordan.mosio@hotmail.fr");
		when(request.getParameter("pwd")).thenReturn("mydummypwd");
		
		// Should not throw an exception
		servelet.doTestPost(request, response);
		
		// Success
		verify(request).getRequestDispatcher(eq("/public/succes_creation.jsp"));
		verify(request, never()).getRequestDispatcher(eq("/public/server_error.jsp"));
		verify(request, never()).getRequestDispatcher(eq("/public/creation_compte.jsp"));
	}
	
	
	// Utils classes

	private final class RequestDispatcherForTest implements RequestDispatcher {
		@Override
		public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		}
		@Override
		public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		}
	}

	@SuppressWarnings("serial")
	private class TestServlet extends CreationCompte {
		protected void doTestPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			super.doPost(request, response);
		}
	}
}
