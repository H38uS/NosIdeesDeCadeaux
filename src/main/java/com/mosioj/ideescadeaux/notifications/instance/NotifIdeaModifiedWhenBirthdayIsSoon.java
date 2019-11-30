package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;

public class NotifIdeaModifiedWhenBirthdayIsSoon extends AbstractNotification {
	
	public static final int NB_DAYS_BEFORE_BIRTHDAY = 5;

	private String userName;
	private String idea;
	private boolean isNew;

	/**
	 * 
	 * @param user L'utilisateur qui a modifié ses idées.
	 * @param idea
	 * @param isNew True si elle est nouvelle, false si c'est une ancienne.
	 */
	public NotifIdeaModifiedWhenBirthdayIsSoon(User user, Idee idea, boolean isNew) {
		super(NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON);
		this.userName = user.getName();
		int size = 50;
		this.idea = idea.getTextSummary(size);
		this.isNew = isNew;
		params.put(ParameterName.USER_ID, user.id);
		params.put(ParameterName.IDEA_ID, idea.getId());
		params.put(ParameterName.IS_NEW, isNew);
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifIdeaModifiedWhenBirthdayIsSoon(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/voir_liste?id=" + params.get(ParameterName.USER_ID) + "\">sa liste</a>";
		String action = isNew ? "ajouté une nouvelle" : "modifié son";
		return MessageFormat.format("{0} a {3} idée \"{1}\". Consulter {2}.",
									userName,
									idea,
									link,
									action);
	}

}
