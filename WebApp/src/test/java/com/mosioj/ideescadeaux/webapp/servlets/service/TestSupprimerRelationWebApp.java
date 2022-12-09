package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Test;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.ACCEPTED_FRIENDSHIP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        Notification notification = ACCEPTED_FRIENDSHIP.with(moiAutre).sendItTo(firefox);
        assertNotifDoesExists(notification);

        bindPostRequestParam(ServiceSupprimerRelation.USER_PARAMETER, _MOI_AUTRE_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));
        assertNotifDoesNotExists(notification);
    }

}
