package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.util.Map;

public class NotifBookedRemove extends AbstractNotification {

    private String ideaText;
    private String ideaOwner;

    /**
     * @param idea      The idea.
     * @param ideaOwner The idea owner name.
     */
    public NotifBookedRemove(Idee idea, String ideaOwner) {
        super(NotificationType.BOOKED_REMOVE);
        int size = 150;
        this.ideaText = idea.getTextSummary(size);
        this.ideaOwner = ideaOwner;
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifBookedRemove(int id,
                             User owner,
                             String text,
                             Timestamp creationTime,
                             boolean isUnread,
                             Timestamp readOn,
                             Map<ParameterName, Object> parameters) {
        super(NotificationType.BOOKED_REMOVE, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return ideaOwner + " a supprimé son idée : \"" + ideaText + "\"";
    }

}
