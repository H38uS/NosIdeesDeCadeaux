package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.util.Map;

public class NotifDemandeAcceptee extends AbstractNotification {

    private String userName;

    public NotifDemandeAcceptee(int fromUser, String userName) {
        super(NotificationType.ACCEPTED_FRIENDSHIP);
        this.userName = userName;
        params.put(ParameterName.USER_ID, fromUser);
    }

    public NotifDemandeAcceptee(int id,
                                User owner,
                                String text,
                                Timestamp creationTime,
                                boolean isUnread,
                                Timestamp readOn,
                                Map<ParameterName, Object> parameters) {
        super(NotificationType.ACCEPTED_FRIENDSHIP, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return userName + " a accepté votre demande d'ami !";
    }

}
