package com.mosioj.notifications.instance;

import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifIdeaModifiedWhenBirthdayIsSoon extends AbstractNotification {

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
		this.userName = user.name;
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
	public NotifIdeaModifiedWhenBirthdayIsSoon(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/mes_listes\">mes listes</a>";
		String action = isNew ? "ajouté une nouvelle" : "modifié son";
		return MessageFormat.format("{0} a {3} idée \"{1}\". Consulter {2}.",
									userName,
									idea,
									link,
									action); // TODO : pouvoir afficher uniquement sa liste
	}

}
