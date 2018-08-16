package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.notifications.instance.NotifDemandeRefusee;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.servlets.controllers.relations.AfficherReseau;
import com.mosioj.servlets.controllers.relations.ResoudreDemandeAmi;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestResoudreDemandeAmi extends AbstractTestServlet {

	public TestResoudreDemandeAmi() {
		super(new ResoudreDemandeAmi());
	}
	
	@Test
	public void testImpossibleToAcceptIfNotAsked() throws ServletException, IOException, SQLException {

		userRelations.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
		assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));

		when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("choix_" + _MOI_AUTRE_, new String[] {"Accepter"});
		when(request.getParameterMap()).thenReturn(params);
		
		doTestPost(request, response);

		assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
	}

	@Test
	public void testAcceptationAmitieEtSuppressionNotif() throws ServletException, IOException, SQLException {
		
		userRelations.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
		assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));

		// Ajout des notifs
		int n1 = notif.addNotification(_OWNER_ID_, new NotifDemandeRefusee(_MOI_AUTRE_, "Moi Autre"));
		int n2 = notif.addNotification(_MOI_AUTRE_, new NotifFriendshipDropped(_OWNER_ID_, "Firefox"));
		assertNotifDoesExists(n1);
		assertNotifDoesExists(n2);
		
		// Ajout de la demande d'ami
		userRelationRequests.insert(_MOI_AUTRE_, _OWNER_ID_);

		when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("choix_" + _MOI_AUTRE_, new String[] {"Accepter"});
		when(request.getParameterMap()).thenReturn(params);
		doTestPost(request, response);

		assertTrue(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
		assertNotifDoesNotExists(n1);
		assertNotifDoesNotExists(n2);
	}

}
