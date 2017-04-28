package com.mosioj.notifications.instance;

import java.util.Map;

import com.mosioj.model.Idee;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

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
	public NotifBookedRemove(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.BOOKED_REMOVE, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		return ideaOwner + " a supprimé son idée : \"" + ideaText + "\"";
	}

}
