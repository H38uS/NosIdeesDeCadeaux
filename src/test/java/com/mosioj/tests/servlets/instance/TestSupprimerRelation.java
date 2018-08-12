package com.mosioj.tests.servlets.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.mosioj.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.servlets.controllers.relations.SupprimerRelation;
import com.mosioj.tests.servlets.AbstractTestServlet;

public class TestSupprimerRelation extends AbstractTestServlet {

	public TestSupprimerRelation() {
		super(new SupprimerRelation());
	}

	@Test
	public void testSuppressionRelationEtSuppressionNotifs() throws SQLException, ServletException, IOException {
		
		userRelations.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
		assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
		userRelations.addAssociation(_OWNER_ID_, _MOI_AUTRE_);
		assertTrue(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));

		int notifId = notif.addNotification(_OWNER_ID_, new NotifDemandeAcceptee(_MOI_AUTRE_, "Moi Autre"));
		assertEquals(1, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
		
		when(request.getParameter(SupprimerRelation.USER_PARAMETER)).thenReturn(_MOI_AUTRE_ + "");
		doTestGet(request, response);

		assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
		assertEquals(0, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
	}

}
