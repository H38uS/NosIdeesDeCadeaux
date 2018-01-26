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

public class NotifGroupSuggestion extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	private int groupId;
	private String idea;

	/**
	 * 
	 * @param user
	 * @param groupId
	 * @param idea
	 */
	public NotifGroupSuggestion(User user, int groupId, Idee idea) {
		super(NotificationType.GROUP_IDEA_SUGGESTION);
		this.userName = user.name;
		this.groupId = groupId;
		int size = 50;
		this.idea = idea.getTextSummary(size);
		params.put(ParameterName.USER_ID, user.id);
		params.put(ParameterName.IDEA_ID, idea.getId());
		params.put(ParameterName.GROUP_ID, groupId);
	}

	/**
	 * 
	 * @param id The internal database ID.
	 * @param owner The notification owner.
	 * @param text The notification text.
	 * @param parameters The notification parameters.
	 */
	public NotifGroupSuggestion(int id, int owner, String text, Timestamp creationTime, Map<ParameterName, Object> parameters) {
		super(NotificationType.GROUP_IDEA_SUGGESTION, id, owner, text, parameters, creationTime);
	}

	@Override
	public String getTextToInsert() {
		final String link = MessageFormat.format("<a href=\"protected/detail_du_groupe?groupid={0}\">ici</a>", groupId);
		return MessageFormat.format("{0} vous suggère un groupe sur l''idée \"{1}\". Cliquez {2} pour participer !",
									userName,
									idea,
									link);
	}

	@Override
	public int getUserIdParam() {
		return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
	}

}
