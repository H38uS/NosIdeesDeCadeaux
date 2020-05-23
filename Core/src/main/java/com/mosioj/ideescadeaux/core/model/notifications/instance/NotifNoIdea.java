package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.util.Map;

public class NotifNoIdea extends AbstractNotification {

    public NotifNoIdea() {
        super(NotificationType.NO_IDEA);
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifNoIdea(int id,
                       User owner,
                       String text,
                       Timestamp creationTime,
                       boolean isUnread,
                       Timestamp readOn,
                       Map<ParameterName, Object> parameters) {
        super(NotificationType.NO_IDEA, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return "Vous n'avez pas encore d'idée !";
    }

}
