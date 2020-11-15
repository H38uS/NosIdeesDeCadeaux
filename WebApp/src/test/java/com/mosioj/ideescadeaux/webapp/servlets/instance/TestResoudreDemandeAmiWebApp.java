package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.AfficherReseau;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.ResoudreDemandeAmi;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestResoudreDemandeAmiWebApp extends AbstractTestServletWebApp {

    public TestResoudreDemandeAmiWebApp() {
        super(new ResoudreDemandeAmi());
    }

    @Test
    public void testImpossibleToAcceptIfNotAsked() throws SQLException {

        UserRelationsRepository.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertFalse(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));

        when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");

        Map<String, String[]> params = new HashMap<>();
        params.put("choix_" + _MOI_AUTRE_, new String[]{"Accepter"});
        when(request.getParameterMap()).thenReturn(params);

        doTestPost();

        assertFalse(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));
    }

    @Test
    public void testAcceptationAmitieEtSuppressionNotif() throws SQLException {

        UserRelationsRepository.deleteAssociation(_OWNER_ID_, _MOI_AUTRE_);
        assertFalse(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));

        // Ajout des notifs
        int n1 = REJECTED_FRIENDSHIP.with(moiAutre).sendItTo(firefox);
        int n2 = NType.FRIENDSHIP_DROPPED.with(firefox).sendItTo(moiAutre);
        int newRelationSuggestion = NEW_RELATION_SUGGESTION.with(moiAutre).sendItTo(firefox);
        int notRemoved = NEW_RELATION_SUGGESTION.with(friendOfFirefox).sendItTo(firefox);
        int newFriendshipRequest = NEW_FRIENSHIP_REQUEST.with(moiAutre).sendItTo(firefox);
        assertNotifDoesExists(n1);
        assertNotifDoesExists(n2);
        assertNotifDoesExists(newRelationSuggestion);
        assertNotifDoesExists(notRemoved);
        assertNotifDoesExists(newFriendshipRequest);

        // Ajout de la demande d'ami
        UserRelationRequestsRepository.insert(moiAutre, firefox);

        when(request.getParameter(AfficherReseau.USER_ID_PARAM)).thenReturn(_MOI_AUTRE_ + "");
        Map<String, String[]> params = new HashMap<>();
        params.put("choix_" + _MOI_AUTRE_, new String[]{"Accepter"});
        when(request.getParameterMap()).thenReturn(params);
        doTestPost();

        assertTrue(UserRelationsRepository.associationExists(_OWNER_ID_, _MOI_AUTRE_));
        assertNotifDoesNotExists(n1);
        assertNotifDoesNotExists(n2);
        assertNotifDoesNotExists(newRelationSuggestion);
        assertNotifDoesExists(notRemoved);
        assertNotifDoesNotExists(newFriendshipRequest);
    }

}
