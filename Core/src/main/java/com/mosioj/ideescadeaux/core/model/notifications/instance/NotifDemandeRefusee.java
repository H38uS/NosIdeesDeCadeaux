package com.mosioj.ideescadeaux.core.model.notifications.instance;

import java.sql.Timestamp;
import java.util.Map;

import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

public class NotifDemandeRefusee extends AbstractNotification {
	
	private String userName;

	public NotifDemandeRefusee(int fromUser, String userName) {
		super(NotificationType.REJECTED_FRIENDSHIP);
		this.userName = userName;
		params.put(ParameterName.USER_ID, fromUser);
	}

	public NotifDemandeRefusee(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.REJECTED_FRIENDSHIP, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		return userName + " a refusé votre demande d'ami...";
	}

}
