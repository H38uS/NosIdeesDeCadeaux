package com.mosioj.notifications.instance;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;

public class NotifBookedRemove extends AbstractNotification {

	private String text;
	private String owner;
	
	public NotifBookedRemove(String ideaText, String ideaOwner) {
		super(NotificationType.BOOKED_REMOVE);
		int size = 150;
		text = ideaText.length() > size ? ideaText.substring(0, size - 3) + "..." : ideaText;
		owner = ideaOwner;
	}

	@Override
	public String getText() {
		return owner + " a supprimé son idée : \"" + text + "\"";
	}

}
