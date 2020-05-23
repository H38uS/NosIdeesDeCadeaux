package com.mosioj.ideescadeaux.core.model.notifications.instance;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

import java.sql.Timestamp;
import java.util.Map;

public class NotifAdministration extends AbstractNotification {

    public NotifAdministration(int id,
                               User owner,
                               String text,
                               Map<ParameterName, Object> parameters,
                               Timestamp creationTime,
                               boolean isUnread,
                               Timestamp readOn) {
        super(null, id, owner, text, parameters, creationTime, isUnread, readOn);
    }

    @Override
    public String getTextToInsert() {
        return "";
    }

}
