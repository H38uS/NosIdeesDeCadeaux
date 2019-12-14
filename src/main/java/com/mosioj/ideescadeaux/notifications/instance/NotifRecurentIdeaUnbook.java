package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;

public class NotifRecurentIdeaUnbook extends AbstractNotification {
	
	private String userName;
	private String idea;

	/**
	 * 
	 * @param user L'utilisateur qui a modifié son idée.
	 * @param idea
	 */
	public NotifRecurentIdeaUnbook(User user, Idee idea) {
		super(NotificationType.RECURENT_IDEA_UNBOOK);
		this.userName = user.getName();
		int size = 50;
		this.idea = idea.getTextSummary(size);
		params.put(ParameterName.USER_ID, user.id);
		params.put(ParameterName.IDEA_ID, idea.getId());
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifRecurentIdeaUnbook(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.RECURENT_IDEA_UNBOOK, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/voir_liste?id=" + params.get(ParameterName.USER_ID) + "\">sa liste</a>";
		return MessageFormat.format("{0} souhaite toujours recevoir son idée \"{1}\". Consulter {2}.",
									userName,
									idea,
									link);
	}

}