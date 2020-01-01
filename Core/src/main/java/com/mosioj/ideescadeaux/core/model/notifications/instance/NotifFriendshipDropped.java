package com.mosioj.ideescadeaux.core.model.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

public class NotifFriendshipDropped extends AbstractNotification {

	private String userName;

	/**
	 * 
	 */
	public NotifFriendshipDropped(User fromUser) {
		super(NotificationType.FRIENDSHIP_DROPPED);
		this.userName = fromUser.getName();
		params.put(ParameterName.USER_ID, fromUser.id);
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifFriendshipDropped(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.FRIENDSHIP_DROPPED, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return userName + " a mis fin à votre relation...";
	}

}
