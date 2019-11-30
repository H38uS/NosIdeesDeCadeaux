package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
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

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.MaListe;
import com.mosioj.ideescadeaux.utils.database.NoRowsException;
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

		verify(session).setAttribute(eq("added_idea_id"), anyObject());
		verify(request, never()).setAttribute(eq("errors"), anyObject());
		assertNotifDoesNotExists(noIdea);
	}

	@Test
	public void testShouldAutoConvertLinks() throws ServletException, IOException, SQLException, NoRowsException {

		Map<String, String> param = new HashMap<String, String>();
		param.put("text", "un lien https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra et voilà");
		param.put("priority", "1");
		createMultiPartRequest(param);
		doTestPost(request, response);

		int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
		Idee idee = idees.getIdeaWithoutEnrichment(id);
		assertEquals(	"un lien <a href=\"https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra\" target=\"_blank\">https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra</a> et voilà",
						idee.getText());
	}
}
