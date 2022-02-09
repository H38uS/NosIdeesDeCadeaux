package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.ACCEPTED_FRIENDSHIP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestSupprimerRelationWebApp extends AbstractTestServletWebApp {

    public TestSupprimerRelationWebApp() {
        super(new ServiceSupprimerRelation());
    }

    @Test
    public void testSuppressionRelationEtSuppressionNotifs() {

        UserRelationsRepository.deleteAssociation(firefox, moiAutre);
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));
        UserRelationsRepository.addAssociation(firefox, moiAutre);
        assertTrue(UserRelationsRepository.associationExists(firefox, moiAutre));

        int notifId = ACCEPTED_FRIENDSHIP.with(moiAutre).sendItTo(firefox);
        assertNotifDoesExists(notifId);

        when(request.getParameter(ServiceSupprimerRelation.USER_PARAMETER)).thenReturn(_MOI_AUTRE_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));
        assertNotifDoesNotExists(notifId);
    }

}
