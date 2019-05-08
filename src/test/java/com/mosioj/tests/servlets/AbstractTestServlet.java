package com.mosioj.tests.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.mobile.device.Device;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.compte.CreationCompte;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.tests.TemplateTest;

public abstract class AbstractTestServlet extends TemplateTest {

	protected RequestDispatcher dispatcher;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	protected Device device;

	protected final IdeesCadeauxServlet<? extends SecurityPolicy> instance;

	public AbstractTestServlet(IdeesCadeauxServlet<? extends SecurityPolicy> pInstance) {

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		device = mock(Device.class);

		when(request.getSession()).thenReturn(session);
		when(request.getRequestURL()).thenReturn(new StringBuffer(CreationCompte.HTTP_LOCALHOST_8080));
		when(request.getContextPath()).thenReturn("");
		when(session.getAttribute("connected_user")).thenReturn(firefox);
		when(session.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		when(request.getAttribute("device")).thenReturn(device);
		when(request.getRequestDispatcher("/protected/erreur_parametre_ou_droit.jsp")).thenReturn(dispatcher);
		try {
			when(response.getOutputStream()).thenReturn(new MyServerOutput());
		} catch (IOException e) {
			e.printStackTrace();
		}

		instance = pInstance;
		instance.setIdeaPicturePath(new File("C:\\temp"));
		
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
	 * 
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
	protected void doTestPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		when(request.getMethod()).thenReturn("POST");
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
	protected void doTestGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		when(request.getMethod()).thenReturn("GET");
		instance.doGet(request, response);
	}

	protected void createMultiPartRequest(Map<String,String> parameters) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		String token = "------WebKitFormBoundaryuTTrxbWL6cN4Eumf";
		String CR_LF = "\r\n";
		
		sb.append("--");
		sb.append(token);
		for (String param : parameters.keySet()) {
			
			sb.append(CR_LF);
			
			sb.append("Content-Disposition: form-data; name=\"");
			sb.append(param);
			sb.append("\"");
			sb.append(CR_LF);
			sb.append(CR_LF);
			
			sb.append(parameters.get(param));
			sb.append(CR_LF);
			sb.append("--");
			sb.append(token);
		}
		final String content = sb.toString();
		final byte[] byteContent = content.getBytes();
		
		ServletInputStream sis = new ServletInputStream() {
			
			int pos = -1;
			
			@Override
			public int read() throws IOException {
				pos++;
				if (pos >= byteContent.length) {
					return -1;
				}
				return byteContent[pos];
			}
			
			@Override
			public void setReadListener(ReadListener readListener) {
			}
			
			@Override
			public boolean isReady() {
				return true;
			}
			
			@Override
			public boolean isFinished() {
				return true;
			}
		};
	
		when(request.getContentType()).thenReturn("multipart/form-data; boundary=" + token);
		when(request.getInputStream()).thenReturn(sis);
		when(request.getContentLength()).thenReturn(byteContent.length);
	}

	private class MyServerOutput extends ServletOutputStream {

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
		}

		@Override
		public void write(int b) throws IOException {
		}
		
	}
}
