package com.mosioj.notifications.instance;

import java.text.MessageFormat;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifConfirmedUpToDate extends AbstractNotification {

	private final User ideaOwner;
	private final String ideaText;

	/**
	 * 
	 * @param askedUser
	 * @param ideaId
	 */
	public NotifConfirmedUpToDate(User ideaOwner, Idee idea) {
		super(NotificationType.CONFIRMED_UP_TO_DATE);
		this.ideaOwner = ideaOwner;
		this.ideaText = idea.getTextSummary(50);
		params.put(ParameterName.USER_ID, ideaOwner.id + "");
		params.put(ParameterName.IDEA_ID, idea.getId() + "");
	}

	@Override
	public String getText() {
		return MessageFormat.format("{0} confirme que son idée \"{1}\" est maintenant à jour !", ideaOwner.name, ideaText);
	}

}
