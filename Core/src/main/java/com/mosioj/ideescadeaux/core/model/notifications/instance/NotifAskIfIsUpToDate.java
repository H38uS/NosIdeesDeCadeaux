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

public class NotifAskIfIsUpToDate extends AbstractNotification implements NotifUserIdParam {

    private String ideaText;
    private int ideaId;

    /**
     * @param askingUser User who asked whether this idea is up to date or not.
     * @param idea      The idea.
     */
    public NotifAskIfIsUpToDate(User askingUser, Idee idea) {
        super(NotificationType.IS_IDEA_UP_TO_DATE);
        this.ideaText = idea.getTextSummary(50);
        this.ideaId = idea.getId();
        params.put(ParameterName.USER_ID, askingUser.id);
        params.put(ParameterName.IDEA_ID, idea.getId());
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifAskIfIsUpToDate(int id,
                                User owner,
                                String text,
                                Timestamp creationTime,
                                boolean isUnread,
                                Timestamp readOn,
                                Map<ParameterName, Object> parameters) {
        super(NotificationType.IS_IDEA_UP_TO_DATE, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {

        String param = "idee=" + ideaId;
        String oui = MessageFormat.format("<li><a href=\"protected/confirmation_est_a_jour?{0}\">Oui !</a></li>",
                                          param);

        param = "idee=" + ideaId;
        String nonSupr = MessageFormat.format(
                "<li>Non... Il faudrait la <a href=\"protected/remove_an_idea?{0}&from=/protected/mes_notifications\">supprimer</a>.</li>",
                param);

        param = "id=" + ideaId;
        String nonModif = MessageFormat.format(
                "<li>Non... Je la <a href=\"protected/modifier_idee?{0}\">modifie</a> de suite !</li>",
                param);

        return MessageFormat.format(
                "Quelqu''un souhaiterait savoir si votre idée \"{0}\" est toujours à jour. <ul>{1}{2}{3}</ul>",
                ideaText,
                oui,
                nonSupr,
                nonModif);
    }

    @Override
    public int getUserIdParam() {
        return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
    }

}
