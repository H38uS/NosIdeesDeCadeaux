package com.mosioj.ideescadeaux.core.model.notifications;

import java.util.List;

import com.mosioj.ideescadeaux.core.model.entities.User;

public class ChildNotifications {

	private final User child;
	private final List<AbstractNotification> notifications;

	/**
	 * 
	 * @param child The child user
	 * @param notifications The child notifications.
	 */
	public ChildNotifications(User child, List<AbstractNotification> notifications) {
		this.child = child;
		this.notifications = notifications;
	}

	/**
	 * 
	 * @return The child name.
	 */
	public String getName() {
		return child.getName();
	}

	/**
	 * 
	 * @return The child notifications.
	 */
	public List<AbstractNotification> getNotifications() {
		return notifications;
	}
}
