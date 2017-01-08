package com.mosioj.notifications;

public abstract class Notification {

	/**
	 * The notification type.
	 */
	private final NotificationType type;

	public Notification(NotificationType type) {
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

}
