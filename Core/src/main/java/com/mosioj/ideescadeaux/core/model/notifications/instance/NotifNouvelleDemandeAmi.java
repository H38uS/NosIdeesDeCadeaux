package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

public class NotifNouvelleDemandeAmi extends AbstractNotification {

    private String userName;
    private int toUserID;

    /**
     * @param fromUser L'utilisateur qui souhaite être ami avec toUserID
     * @param toUserID La personne à qui on demande d'être ami
     * @param userName Le nom de fromUser
     */
    public NotifNouvelleDemandeAmi(User fromUser, int toUserID, String userName) {
        super(NotificationType.NEW_FRIENSHIP_REQUEST);
        this.userName = fromUser.getName();
        this.toUserID = toUserID;
        params.put(ParameterName.USER_ID, fromUser.id);
    }

    public NotifNouvelleDemandeAmi(int id,
                                   User owner,
                                   String text,
                                   Timestamp creationTime,
                                   boolean isUnread,
                                   Timestamp readOn,
                                   Map<ParameterName, Object> parameters) {
        super(NotificationType.NEW_FRIENSHIP_REQUEST, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        final String link = "<a href=\"protected/afficher_reseau?id=" + toUserID + "\">Voir les demandes en cours</a>";
        return MessageFormat.format("{0} vous a envoyé une demande d''ami ! {1}.", userName, link);
    }

}
