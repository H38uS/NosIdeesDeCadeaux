package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.param.NotifUserIdParam;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

public class NotifConfirmedUpToDate extends AbstractNotification implements NotifUserIdParam {

    private User ideaOwner;
    private String ideaText;

    /**
     * To build a new notification.
     *
     * @param ideaOwner The idea owner.
     * @param idea The idea.
     */
    public NotifConfirmedUpToDate(User ideaOwner, Idee idea) {
        super(NotificationType.CONFIRMED_UP_TO_DATE);
        this.ideaOwner = ideaOwner;
        this.ideaText = idea.getTextSummary(50);
        params.put(ParameterName.USER_ID, ideaOwner.id);
        params.put(ParameterName.IDEA_ID, idea.getId());
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifConfirmedUpToDate(int id,
                                  User owner,
                                  String text,
                                  Timestamp creationTime,
                                  boolean isUnread,
                                  Timestamp readOn,
                                  Map<ParameterName, Object> parameters) {
        super(NotificationType.CONFIRMED_UP_TO_DATE, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return MessageFormat.format("{0} confirme que son idée \"{1}\" est maintenant à jour !",
                                    ideaOwner.getName(),
                                    ideaText);
    }

    @Override
    public int getUserIdParam() {
        return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
    }

}
