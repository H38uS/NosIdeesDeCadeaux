package com.mosioj.notifications;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNotification {

	/**
	 * The notification type.
	 */
	private final NotificationType type;

	protected Map<String, String> params = new HashMap<String, String>();

	public AbstractNotification(NotificationType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return The notification type.
	 */
	public String getType() {
		return type.name();
	}

	/**
	 * 
	 * @return The notification text.
	 */
	public abstract String getText();

	/**
	 * Send the notification by email.
	 */
	public void sendEmail(String emailAdress) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @return The parameter list of this notification.
	 */
	public Map<String, String> getParameters() {
		return params;
	}

}
