package com.mosioj.tests.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.BeforeClass;
import org.springframework.mobile.device.Device;

import com.mosioj.model.table.Idees;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.compte.CreationCompte;
import com.mosioj.tests.TemplateTest;
import com.mosioj.utils.database.DataSourceIdKDo;
import com.mosioj.utils.database.NoRowsException;
import com.mysql.cj.jdbc.MysqlDataSource;

public abstract class AbstractTestServlet extends TemplateTest {

	protected static final int _OWNER_ID_ = 26; // firefox@toto.com

	protected RequestDispatcher dispatcher;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	protected Device device;

	protected final IdeesCadeauxServlet instance;
	protected static DataSourceIdKDo ds;
	
	protected final Idees idees = new Idees();

	@BeforeClass
	public static void init() throws NamingException, SQLException, NoRowsException {

		MysqlDataSource dataSource = new MysqlDataSource();
		
		dataSource.setDatabaseName("test_ideeskdos");
		dataSource.setUser("mosioj");
		dataSource.setPassword("tuaD50Kv2jguyX5ncokK");
		dataSource.setURL("jdbc:mysql://192.168.1.44/test_ideeskdos?serverTimezone=Europe/Paris");

		DataSourceIdKDo.setDataSource(dataSource);
		ds = new DataSourceIdKDo();
		String email = ds.selectString("select email from USERS where id = ?", 3);
		assertEquals("ymosio@wanadzdzdzdoo.fr", email);
	}

	public AbstractTestServlet(IdeesCadeauxServlet pInstance) {

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
		dispatcher = mock(RequestDispatcher.class);
		device = mock(Device.class);

		when(request.getSession()).thenReturn(session);
		when(request.getRequestURL()).thenReturn(new StringBuffer(CreationCompte.HTTP_LOCALHOST_8080));
		when(request.getContextPath()).thenReturn("");
		when(session.getAttribute("userid")).thenReturn(_OWNER_ID_);
		when(session.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
		when(request.getAttribute("device")).thenReturn(device);

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
	protected void doTestGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		instance.doGet(req, resp);
	}

}
