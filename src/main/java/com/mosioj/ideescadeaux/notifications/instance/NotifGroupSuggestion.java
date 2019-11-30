package com.mosioj.ideescadeaux.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.param.NotifUserIdParam;

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
		this.userName = user.getName();
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
	public NotifGroupSuggestion(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.GROUP_IDEA_SUGGESTION, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = "<a href=\"protected/detail_du_groupe?groupid=" + groupId + "\">ici</a>";
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
