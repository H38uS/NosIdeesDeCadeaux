package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;

public class NotifBookedRemove extends AbstractNotification {

	private String ideaText;
	private String ideaOwner;

	/**
	 * 
	 * @param ideaText
	 * @param ideaOwner
	 */
	public NotifBookedRemove(Idee idea, String ideaOwner) {
		super(NotificationType.BOOKED_REMOVE);
		int size = 150;
		this.ideaText = idea.getTextSummary(size);
		this.ideaOwner = ideaOwner;
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifBookedRemove(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.BOOKED_REMOVE, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return ideaOwner + " a supprimé son idée : \"" + ideaText + "\"";
	}

}
