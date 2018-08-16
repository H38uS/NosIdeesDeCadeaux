package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.model.Idee;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.servlets.controllers.idees.modification.ModifyIdea;
import com.mosioj.tests.servlets.AbstractTestServlet;
import com.mosioj.utils.database.NoRowsException;

public class TestModifyIdea extends AbstractTestServlet {

	public TestModifyIdea() {
		super(new ModifyIdea());
	}

	@Test
	public void testModifyRemovesCorrectNotification() throws SQLException, NoRowsException, ServletException, IOException {
		
		int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
		String newText = "Idee modifiee le " + new Date();
		Idee idee = idees.getIdea(id);
		assertFalse(newText.equals(idee.text));
		
		int notifId = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
		int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
		assertNotifDoesExists(notifId);
		assertNotifDoesExists(addByFriend);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("text", newText);
		param.put("type", "");
		param.put("priority", 2+"");
		createMultiPartRequest(param);
		when(request.getParameter(ModifyIdea.IDEE_ID_PARAM)).thenReturn(id+"");
		doTestPost(request, response);

		idee = idees.getIdea(id);
		assertEquals(newText, idee.text);
		assertNotifDoesNotExists(notifId);
		assertNotifDoesNotExists(addByFriend);
	}

}
