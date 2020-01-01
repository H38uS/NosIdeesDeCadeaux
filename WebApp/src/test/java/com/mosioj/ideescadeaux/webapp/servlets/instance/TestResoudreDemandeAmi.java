package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifDemandeRefusee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifFriendshipDropped;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.AfficherReseau;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.ResoudreDemandeAmi;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestResoudreDemandeAmi extends AbstractTestServlet {

    public TestResoudreDemandeAmi() {
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
        int n1 = NotificationsRepository.addNotification(_OWNER_ID_, new NotifDemandeRefusee(_MOI_AUTRE_, "Moi Autre"));
        int n2 = NotificationsRepository.addNotification(_MOI_AUTRE_, new NotifFriendshipDropped(firefox));
        int newRelationSuggestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                            new NotifNewRelationSuggestion(_MOI_AUTRE_,
                                                                                                 "Friend of firefox"));
        int notRemoved = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                 new NotifNewRelationSuggestion(_FRIEND_ID_,
                                                                                      "Friend of firefox"));
        int newFriendshipRequest = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                           new NotifNouvelleDemandeAmi(moiAutre,
                                                                                             _OWNER_ID_,
                                                                                             "Moi autre"));
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
