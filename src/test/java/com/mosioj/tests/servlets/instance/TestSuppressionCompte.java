package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.User;
import com.mosioj.servlets.service.ServiceSuppressionCompte;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestSuppressionCompte extends AbstractTestServlet {

	public TestSuppressionCompte() {
		super(new ServiceSuppressionCompte());
	}

	@Test
	public void testSuccess() throws SQLException, ServletException, IOException {

		when(request.isUserInRole("ROLE_ADMIN")).thenReturn(true);
		assertTrue(request.isUserInRole("ROLE_ADMIN"));
		
		int userId = users.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
		assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));

		when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(userId + "");
		doTestPost(request, response);
		assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
	}

	@Test
	public void testNotAdmin() throws SQLException, ServletException, IOException {

		assertFalse(request.isUserInRole("ROLE_ADMIN"));
		
		int userId = users.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
		assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
		User user = users.getUser(userId);

		when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(userId + "");
		doTestPost(request, response);
		assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
		users.deleteUser(user);
		assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
	}

}
