package com.mosioj.notifications;

import java.util.Map;

import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifBookedRemove;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.notifications.instance.NotifNoIdea;

public class NotificationFactory {

	private NotificationFactory() {
	}

	/**
	 * 
	 * @param id
	 * @param owner
	 * @param type
	 * @param text
	 * @param notifParams
	 * @return A new notification object based on the database content.
	 */
	public static AbstractNotification buildIt(int id, int owner, String type, String text, Map<ParameterName, Object> params) {

		NotificationType t = NotificationType.valueOf(type);

		switch (t) {
		case BOOKED_REMOVE:
			return new NotifBookedRemove(id, owner, text, params);

		case CONFIRMED_UP_TO_DATE:
			return new NotifConfirmedUpToDate(id, owner, text, params);

		case GROUP_IDEA_SUGGESTION:
			return new NotifGroupSuggestion(id, owner, text, params);

		case IS_IDEA_UP_TO_DATE:
			return new NotifAskIfIsUpToDate(id, owner, text, params);

		case NO_IDEA:
			return new NotifNoIdea(id, owner, text, params);

		default:
			return null;
		}
	}
}
