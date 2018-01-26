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
		this.userName = user.name;
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
	public NotifIdeaAddedByFriend(int id, int owner, String text, Timestamp creationTime, Map<ParameterName, Object> parameters) {
		super(NotificationType.IDEA_ADDED_BY_FRIEND, id, owner, text, parameters, creationTime);
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
