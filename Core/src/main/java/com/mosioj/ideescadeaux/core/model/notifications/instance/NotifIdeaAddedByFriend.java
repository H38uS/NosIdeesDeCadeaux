package com.mosioj.ideescadeaux.core.model.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.ideescadeaux.core.model.notifications.instance.param.NotifUserIdParam;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

public class NotifIdeaAddedByFriend extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	private String idea;

	/**
	 * 
	 * @param user La personne qui ajoute l'idée.
	 * @param groupId
	 * @param idea
	 */
	public NotifIdeaAddedByFriend(User user, Idee idea) {
		super(NotificationType.IDEA_ADDED_BY_FRIEND);
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
	public NotifIdeaAddedByFriend(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.IDEA_ADDED_BY_FRIEND, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/ma_liste\">ma liste</a>";
		return MessageFormat.format("{0} vous a ajouté une nouvelle idée \"{1}\". Consulter {2}.",
									userName,
									idea,
									link);
	}

	@Override
	public int getUserIdParam() {
		return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
	}

}
