package com.mosioj.notifications.instance;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;

public class NotifNoIdea extends AbstractNotification {

	public NotifNoIdea() {
		super(NotificationType.NO_IDEA);
	}

	@Override
	public String getText() {
		return "Vous n'avez pas encore d'idée!";
	}

}
