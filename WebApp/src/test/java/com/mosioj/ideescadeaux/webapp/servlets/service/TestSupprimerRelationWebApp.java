package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestSupprimerRelationWebApp extends AbstractTestServletWebApp {

    public TestSupprimerRelationWebApp() {
        super(new ServiceSupprimerRelation());
    }

    @Test
    public void testSuppressionRelationEtSuppressionNotifs() throws SQLException {

        UserRelationsRepository.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertFalse(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));
        UserRelationsRepository.addAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertTrue(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));

        int notifId = NotificationsRepository.addNotification(_OWNER_ID_, new NotifDemandeAcceptee(_MOI_AUTRE_, "Moi Autre"));
        assertNotifDoesExists(notifId);

        when(request.getParameter(ServiceSupprimerRelation.USER_PARAMETER)).thenReturn(_MOI_AUTRE_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertFalse(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));
        assertNotifDoesNotExists(notifId);
    }

}
