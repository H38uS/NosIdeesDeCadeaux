package com.mosioj.tests.servlets.instance;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.servlets.controllers.compte.UpdateNotificationParameter;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestUpdateNotificationParameter extends AbstractTestServlet {

	public TestUpdateNotificationParameter() {
		super(new UpdateNotificationParameter());
	}

	@Test
	public void testPost() throws ServletException, IOException, SQLException {
		when(request.getParameter("name")).thenReturn("REJECTED_FRIENDSHIP");
		when(request.getParameter("value")).thenReturn("EMAIL");
		instance.doPost(request, response);
		verify(request).getParameter("name");
		verify(request).getParameter("value");
	}

}
