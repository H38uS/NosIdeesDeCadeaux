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

/**
 * Notification envoyée lorsqu'une personne rejoint ou se désabonne du groupe.
 *
 * @author Jordan Mosio
 */
public class NotifGroupEvolution extends AbstractNotification implements NotifUserIdParam {

    private String userName;
    private int groupId;
    private String idea;
    private boolean join;

    /**
     * @param user    The user that join/quit the group.
     * @param groupId The group id.
     * @param idea    The idea.
     * @param join    True if the user has joined the group, false if he has left it.
     */
    public NotifGroupEvolution(User user, int groupId, Idee idea, boolean join) {
        super(NotificationType.GROUP_EVOLUTION);
        this.userName = user.getName();
        this.groupId = groupId;
        final int size = 50;
        this.idea = idea.getTextSummary(size);
        this.join = join;
        params.put(ParameterName.USER_ID, user.id);
        params.put(ParameterName.IDEA_ID, idea.getId());
        params.put(ParameterName.GROUP_ID, groupId);
    }

    /**
     * @param id         The internal database ID.
     * @param owner      The notification owner.
     * @param text       The notification text.
     * @param parameters The notification parameters.
     */
    public NotifGroupEvolution(int id,
                               User owner,
                               String text,
                               Timestamp creationTime,
                               boolean isUnread,
                               Timestamp readOn,
                               Map<ParameterName, Object> parameters) {
        super(NotificationType.GROUP_EVOLUTION, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        final String link = "<a href=\"protected/detail_du_groupe?groupid=" + groupId + "\">ici</a>";
        final String action = join ? "rejoint" : "quitté";
        return MessageFormat.format(
                "{0} a {1} le groupe sur l''idée \"{2}\". Cliquez {3} pour voir le détail du groupe.",
                userName,
                action,
                idea,
                link);
    }

    @Override
    public int getUserIdParam() {
        return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
    }

}
