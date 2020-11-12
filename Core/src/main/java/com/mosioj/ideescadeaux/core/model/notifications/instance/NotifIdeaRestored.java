package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Sent when we have booked an idea and it gets deleted.
 */
public class NotifIdeaRestored extends AbstractNotification {

    private String ideaText;
    private String ideaOwner;

    /**
     * @param idea The idea.
     */
    public NotifIdeaRestored(Idee idea) {
        super(NotificationType.IDEA_RESTORED);
        int size = 150;
        this.ideaText = idea.getTextSummary(size);
        this.ideaOwner = idea.getOwner().getName();
        params.put(ParameterName.IDEA_ID, idea.getId());
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifIdeaRestored(int id,
                             User owner,
                             String text,
                             Timestamp creationTime,
                             boolean isUnread,
                             Timestamp readOn,
                             Map<ParameterName, Object> parameters) {
        super(NotificationType.IDEA_RESTORED, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return ideaOwner + " a restoré son idée : \"" + ideaText + "\".";
    }

}
