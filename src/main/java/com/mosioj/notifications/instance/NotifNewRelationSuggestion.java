package com.mosioj.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.param.NotifUserIdParam;

public class NotifNewRelationSuggestion extends AbstractNotification implements NotifUserIdParam {

	private String userName;
	
	public NotifNewRelationSuggestion(int fromUser, String userName) {
		super(NotificationType.NEW_RELATION_SUGGESTION);
		this.userName = userName;
		params.put(ParameterName.USER_ID, fromUser);
	}

	public NotifNewRelationSuggestion(int id, int owner, String text, Timestamp creationTime, Map<ParameterName, Object> params) {
		super(NotificationType.NEW_RELATION_SUGGESTION, id, owner, text, params, creationTime);
	}

	@Override
	public String getTextToInsert() {
		return userName + " vous a suggérer des amis ! <a href=\"protected/suggestion_amis\">Aller voir</a>...";
	}

	@Override
	public int getUserIdParam() {
		return Integer.parseInt(params.get(ParameterName.USER_ID).toString());
	}

}
