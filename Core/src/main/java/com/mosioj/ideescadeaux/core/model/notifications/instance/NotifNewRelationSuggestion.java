package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.param.NotifUserIdParam;

import java.sql.Timestamp;
import java.util.Map;

public class NotifNewRelationSuggestion extends AbstractNotification implements NotifUserIdParam {

    private String userName;

    /**
     * @param suggestedUserId L'id de l'utilsateur qu'on suggère.
     * @param suggestor       Le nom de l'utilisateur qui suggère la demande d'amitié.
     */
    public NotifNewRelationSuggestion(int suggestedUserId, String suggestor) {
        super(NotificationType.NEW_RELATION_SUGGESTION);
        this.userName = suggestor;
        params.put(ParameterName.USER_ID, suggestedUserId);
    }

    public NotifNewRelationSuggestion(int id,
                                      User owner,
                                      String text,
                                      Timestamp creationTime,
                                      boolean isUnread,
                                      Timestamp readOn,
                                      Map<ParameterName, Object> params) {
        super(NotificationType.NEW_RELATION_SUGGESTION, id, owner, text, params, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return userName + " vous a suggérer des amis ! <a href=\"protected/suggestion_amis\">Aller voir</a>...";
    }

    @Override
    public int getUserIdParam() {
        return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
    }

}
