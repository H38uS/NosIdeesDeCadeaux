package com.mosioj.notifications.instance;

import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifFriendshipDropped extends AbstractNotification {

	private String userName;

	/**
	 * 
	 */
	public NotifFriendshipDropped(int fromUser, String userName) {
		super(NotificationType.FRIENDSHIP_DROPPED);
		this.userName = userName;
		params.put(ParameterName.USER_ID, fromUser);
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifFriendshipDropped(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.FRIENDSHIP_DROPPED, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		return userName + " a mis fin à votre relation...";
	}

}
