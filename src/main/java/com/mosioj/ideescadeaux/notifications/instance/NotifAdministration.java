package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.ParameterName;

public class NotifAdministration extends AbstractNotification {

	public NotifAdministration(int id, int owner, String text, Map<ParameterName, Object> parameters, Timestamp creationTime, boolean isUnread, Timestamp readOn) {
		super(null, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return "";
	}

}
