package com.mosioj.tests.servlets.instance;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import com.mosioj.servlets.controllers.CreationCompte;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.tests.utils.CaptchaMockFactory;
import com.mosioj.utils.RootingsUtils;

import nl.captcha.Captcha;

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

	public void initCaptcha(boolean ok) {
		Captcha captcha = ok ? CaptchaMockFactory.getValidCaptcha(request) : CaptchaMockFactory.getInvalidCaptcha(request);
		when(session.getAttribute(Captcha.NAME)).thenReturn(captcha);
	}

	@Test
	public void testEmptyParameters() throws ServletException, IOException {

		initCaptcha(true);
		
		// Should not throw an exception
		doTestPost(request, response);

		// Test parameters call
		verify(request).getParameter(eq("email"));
		verify(request).getParameter(eq("pwd"));
		verify(request).getParameter(eq("answer")); // Captcha
		verify(request, atMost(3)).getParameter(anyString());

		// Parameters were invalid
		verify(request).setAttribute(eq("email_errors"), anyObject());
		verify(request).setAttribute(eq("pwd_errors"), anyObject());
		verify(request, atMost(2)).setAttribute(anyString(), anyObject());

		// Error in processing
		verify(request).getRequestDispatcher(eq(CreationCompte.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(CreationCompte.SUCCES_URL));
	}

	@Test
	public void testSuccess() throws ServletException, IOException {

		initCaptcha(true);
		
		when(request.getParameter("email")).thenReturn("jordan.mosio@hotmail.fr");
		when(request.getParameter("pwd")).thenReturn("mydummypwd");

		// Should not throw an exception
		doTestPost(request, response);

		// Success
		verify(request).getRequestDispatcher(eq(CreationCompte.SUCCES_URL));
		verify(request, never()).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(CreationCompte.FORM_URL));
	}

	@Test
	public void testInvalidCaptcha() throws ServletException, IOException {

		initCaptcha(false);
		
		when(request.getParameter("email")).thenReturn("jordan.mosio@hotmail.fr");
		when(request.getParameter("pwd")).thenReturn("mydummypwd");

		// Should not throw an exception
		doTestPost(request, response);
		
		// Invalid
		verify(request).getParameter(eq("answer")); // Captcha
		verify(request).getRequestDispatcher(eq(CreationCompte.FORM_URL));
		verify(request, never()).getRequestDispatcher(eq(RootingsUtils.PUBLIC_SERVER_ERROR_JSP));
		verify(request, never()).getRequestDispatcher(eq(CreationCompte.SUCCES_URL));
	}

}
