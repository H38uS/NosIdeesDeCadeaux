package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.AfficherReseau;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceGestionDemandeAmiTest extends AbstractTestServletWebApp {

    public ServiceGestionDemandeAmiTest() {
        super(new ServiceGestionDemandeAmi());
    }

    @Test
    public void testImpossibleToAcceptIfNotAsked() throws SQLException {

        UserRelationsRepository.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));

        when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");

        Map<String, String[]> params = new HashMap<>();
        params.put("choix_" + _MOI_AUTRE_, new String[]{"Accepter"});
        when(request.getParameterMap()).thenReturn(params);

        doTestPost();

        assertFalse(UserRelationsRepository.associationExists(firefox, moiAutre));
    }

    @Test
    public void testAcceptationAmitieEtSuppressionNotif() throws SQLException {

        UserRelationsRepository.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
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
        UserRelationRequestsRepository.insert(moiAutre, firefox);

        when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");
        Map<String, String[]> params = new HashMap<>();
        final String accepted = "acc_choix_" + _MOI_AUTRE_;
        params.put(accepted, new String[]{accepted, "true"});
        when(request.getParameterMap()).thenReturn(params);
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