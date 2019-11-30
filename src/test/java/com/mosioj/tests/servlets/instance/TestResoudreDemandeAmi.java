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

import com.mosioj.ideescadeaux.notifications.instance.NotifDemandeRefusee;
import com.mosioj.ideescadeaux.notifications.instance.NotifFriendshipDropped;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.ideescadeaux.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.ideescadeaux.servlets.controllers.relations.AfficherReseau;
import com.mosioj.ideescadeaux.servlets.controllers.relations.ResoudreDemandeAmi;
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
		int n2 = notif.addNotification(_MOI_AUTRE_, new NotifFriendshipDropped(firefox));
		int newRelationSuggestion = notif.addNotification(_OWNER_ID_, new NotifNewRelationSuggestion(_MOI_AUTRE_, "Friend of firefox"));
		int notRemoved = notif.addNotification(_OWNER_ID_, new NotifNewRelationSuggestion(_FRIEND_ID_, "Friend of firefox"));
		int newFriendshipRequest = notif.addNotification(_OWNER_ID_, new NotifNouvelleDemandeAmi(moiAutre, _OWNER_ID_, "Moi autre"));
		assertNotifDoesExists(n1);
		assertNotifDoesExists(n2);
		assertNotifDoesExists(newRelationSuggestion);
		assertNotifDoesExists(notRemoved);
		assertNotifDoesExists(newFriendshipRequest);
		
		// Ajout de la demande d'ami
		userRelationRequests.insert(moiAutre, firefox);

		when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("choix_" + _MOI_AUTRE_, new String[] {"Accepter"});
		when(request.getParameterMap()).thenReturn(params);
		doTestPost(request, response);

		assertTrue(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
		assertNotifDoesNotExists(n1);
		assertNotifDoesNotExists(n2);
		assertNotifDoesNotExists(newRelationSuggestion);
		assertNotifDoesExists(notRemoved);
		assertNotifDoesNotExists(newFriendshipRequest);
	}

}
