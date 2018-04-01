package com.mosioj.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;

public class NotifErrorOccured extends AbstractNotification {

	public NotifErrorOccured(int id, int owner, String text, Map<ParameterName, Object> parameters, Timestamp creationTime, boolean isUnread, Timestamp readOn) {
		super(null, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return "";
	}

}
