package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.AfficherReseau;
import org.junit.Test;

import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServiceGestionDemandeAmiTest extends AbstractTestServletWebApp {

    public ServiceGestionDemandeAmiTest() {
        super(new ServiceGestionDemandeAmi());
    }

    @Test
    public void testImpossibleToAcceptIfNotAsked() {

        UserRelationsRepository.deleteAssociation(firefox, moiAutre);
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));

        bindRequestParam(AfficherReseau.USER_ID_PARAM, _MOI_AUTRE_ + "");
        bindRequestParamMap(Map.of("choix_" + _MOI_AUTRE_, new String[]{"Accepter"}));

        doTestPost();

        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));
    }

    @Test
    public void testAcceptationAmitieEtSuppressionNotif() {

        UserRelationsRepository.deleteAssociation(firefox, moiAutre);
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));

        // Ajout des notifs
        int n1 = REJECTED_FRIENDSHIP.with(moiAutre).sendItTo(firefox);
        int n2 = NType.FRIENDSHIP_DROPPED.with(firefox).sendItTo(moiAutre);
        int newRelationSuggestion = NEW_RELATION_SUGGESTION.with(moiAutre).sendItTo(firefox);
        int notRemoved = NEW_RELATION_SUGGESTION.with(friendOfFirefox).sendItTo(firefox);
        int newFriendshipRequest = NEW_FRIENSHIP_REQUEST.with(moiAutre).sendItTo(firefox);
        int otherWaynewFriendshipRequest = NEW_FRIENSHIP_REQUEST.with(firefox).sendItTo(moiAutre);
        assertNotifDoesExists(n1);
        assertNotifDoesExists(n2);
        assertNotifDoesExists(newRelationSuggestion);
        assertNotifDoesExists(notRemoved);
        assertNotifDoesExists(newFriendshipRequest);
        assertNotifDoesExists(otherWaynewFriendshipRequest);

        // Ajout de la demande d'ami
        HibernateUtil.saveit(new RelationRequest(moiAutre, firefox));

        final String accepted = "acc_choix_" + _MOI_AUTRE_;
        bindRequestParam(AfficherReseau.USER_ID_PARAM, _MOI_AUTRE_ + "");
        bindRequestParamMap(Map.of(accepted, new String[]{accepted, "true"}));
        doTestPost();

        assertTrue(UserRelationsRepository.associationExists(firefox, moiAutre));
        assertNotifDoesNotExists(n1);
        assertNotifDoesNotExists(n2);
        assertNotifDoesNotExists(newRelationSuggestion);
        assertNotifDoesExists(notRemoved);
        assertNotifDoesNotExists(newFriendshipRequest);
        assertNotifDoesNotExists(otherWaynewFriendshipRequest);
    }
}