package com.mosioj.notifications.instance;

import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifNoIdea extends AbstractNotification {

	/**
	 * 
	 */
	public NotifNoIdea() {
		super(NotificationType.NO_IDEA);
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifNoIdea(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.NO_IDEA, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		return "Vous n'avez pas encore d'idée !";
	}

}
