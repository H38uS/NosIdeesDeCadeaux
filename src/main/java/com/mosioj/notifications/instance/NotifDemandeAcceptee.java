package com.mosioj.notifications.instance;

import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifDemandeAcceptee extends AbstractNotification {

	private String userName;

	public NotifDemandeAcceptee(int fromUser, String userName) {
		super(NotificationType.ACCEPTED_FRIENDSHIP);
		this.userName = userName;
		params.put(ParameterName.USER_ID, fromUser);
	}

	public NotifDemandeAcceptee(int id, int owner, String text, Map<ParameterName, Object> parameters) {
		super(NotificationType.ACCEPTED_FRIENDSHIP, id, owner, text, parameters);
	}

	@Override
	public String getTextToInsert() {
		return userName + " a accepté votre demande d'ami !";
	}

}
