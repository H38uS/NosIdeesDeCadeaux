package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.service.ServiceSupprimerRelation;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestSupprimerRelation extends AbstractTestServlet {

    public TestSupprimerRelation() {
        super(new ServiceSupprimerRelation());
    }

    @Test
    public void testSuppressionRelationEtSuppressionNotifs() throws SQLException {

        userRelations.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
        userRelations.addAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertTrue(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));

        int notifId = notif.addNotification(_OWNER_ID_, new NotifDemandeAcceptee(_MOI_AUTRE_, "Moi Autre"));
        assertNotifDoesExists(notifId);

        when(request.getParameter(ServiceSupprimerRelation.USER_PARAMETER)).thenReturn(_MOI_AUTRE_ + "");
        ServiceResponse resp = doTestServicePost(request, response);

        assertTrue(resp.isOK());
        assertFalse(userRelations.associationExists(_OWNER_ID_, _MOI_AUTRE_));
        assertNotifDoesNotExists(notifId);
    }

}
