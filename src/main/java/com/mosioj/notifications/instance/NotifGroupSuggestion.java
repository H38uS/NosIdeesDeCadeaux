package com.mosioj.notifications.instance;

import java.text.MessageFormat;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;

public class NotifGroupSuggestion extends AbstractNotification {

	private final String userName;
	private final int groupId;
	private final String idea;

	public NotifGroupSuggestion(User user, int groupId, Idee idea) {
		super(NotificationType.GROUP_IDEA_SUGGESTION);
		this.userName = user.name;
		this.groupId = groupId;
		int size = 50;
		this.idea = idea.getTextSummary(size);
		params.put("USER_ID", user.id + "");
		params.put("IDEA_ID", idea.getId() + "");
	}

	@Override
	public String getText() {
		final String link = MessageFormat.format("<a href=\"protected/detail_du_groupe?groupid={0}\">ici</a>", groupId);
		return MessageFormat.format("{0} vous suggère un groupe sur l''idée \"{1}\". Cliquez {2} pour participer !",
									userName,
									idea,
									link);
	}

}
