package com.mosioj.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.param.NotifUserIdParam;

public class NotifConfirmedUpToDate extends AbstractNotification implements NotifUserIdParam {

	private User ideaOwner;
	private String ideaText;

	/**
	 * To build a new notification.
	 * 
	 * @param ideaOwner
	 * @param idea
	 */
	public NotifConfirmedUpToDate(User ideaOwner, Idee idea) {
		super(NotificationType.CONFIRMED_UP_TO_DATE);
		this.ideaOwner = ideaOwner;
		this.ideaText = idea.getTextSummary(50);
		params.put(ParameterName.USER_ID, ideaOwner.id);
		params.put(ParameterName.IDEA_ID, idea.getId());
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifConfirmedUpToDate(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.CONFIRMED_UP_TO_DATE, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return MessageFormat.format("{0} confirme que son idée \"{1}\" est maintenant à jour !", ideaOwner.getName(), ideaText);
	}

	@Override
	public int getUserIdParam() {
		return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
	}

}
