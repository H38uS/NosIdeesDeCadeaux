package com.mosioj.tests.servlets.instance;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.ideescadeaux.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.modification.AjouterIdeeAmi;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestAjouterIdeeAmi extends AbstractTestServlet {

	public TestAjouterIdeeAmi() {
		super(new AjouterIdeeAmi());
	}

	@Test
	public void testSuccess() throws ServletException, IOException, SQLException {

		int noIdea = notif.addNotification(_FRIEND_ID_, new NotifNoIdea());
		assertNotifDoesExists(noIdea);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("text", "Ajouté par un ami");
		param.put("type", "");
		param.put("priority", 2+"");
		createMultiPartRequest(param);
		
		when(request.getRequestDispatcher(AjouterIdeeAmi.VIEW_PAGE_URL)).thenReturn(dispatcher);
		when(request.getParameter(AjouterIdeeAmi.USER_PARAMETER)).thenReturn(_FRIEND_ID_ + "");
		doTestPost(request, response);

		assertNotifDoesNotExists(noIdea);
	}

	@Test
	public void testSuccessSurprise() throws ServletException, IOException, SQLException {
		
		int noIdea = notif.addNotification(_FRIEND_ID_, new NotifNoIdea());
		assertNotifDoesExists(noIdea);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("text", "Ajouté par un ami");
		param.put("type", "");
		param.put("priority", 2+"");
		param.put("est_surprise", "on");
		createMultiPartRequest(param);
		
		when(request.getRequestDispatcher(AjouterIdeeAmi.VIEW_PAGE_URL)).thenReturn(dispatcher);
		when(request.getParameter(AjouterIdeeAmi.USER_PARAMETER)).thenReturn(_FRIEND_ID_ + "");
		doTestPost(request, response);
		
		assertNotifDoesExists(noIdea);
	}

}
