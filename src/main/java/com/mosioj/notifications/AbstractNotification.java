package com.mosioj.notifications;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNotification {
	
	/**
	 * The notification type, useful for database insertion.
	 */
	private NotificationType type;

	public int id;
	public int owner;
	public String text;
	protected Map<ParameterName, Object> params = new HashMap<ParameterName, Object>();

	/**
	 * Default constructor for insertion.
	 */
	public AbstractNotification(NotificationType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @param type The notification type, useful for database insertion.
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public AbstractNotification(NotificationType type, int id, int owner, String text, Map<ParameterName, Object> parameters) {
		this.type = type;
		this.id = id;
		this.owner = owner;
		this.text = text;
		this.params = parameters;
	}
	
	/**
	 * Used in database insertion.
	 * 
	 * @return The notification type.
	 */
	public String getType() {
		return type.name();
	}


	/**
	 * Send the notification by email.
	 */
	public void sendEmail(String emailAdress) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @return The notification text.
	 */
	public abstract String getTextToInsert();

	/**
	 * 
	 * @return The notification text.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * 
	 * @return The parameter list of this notification.
	 */
	public Map<ParameterName, Object> getParameters() {
		return params;
	}

	public int getId() {
		return id;
	}

	public int getOwner() {
		return owner;
	}

}
