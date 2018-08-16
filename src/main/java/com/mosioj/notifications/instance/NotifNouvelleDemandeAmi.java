package com.mosioj.notifications.instance;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;

public class NotifNouvelleDemandeAmi extends AbstractNotification {

	private String userName;
	private int toUserID;

	/**
	 * 
	 * @param fromUser L'utilisateur qui souhaite être ami avec toUserID
	 * @param toUserID La personne à qui on demande d'être ami
	 * @param userName Le nom de fromUser
	 */
	public NotifNouvelleDemandeAmi(int fromUser, int toUserID, String userName) {
		super(NotificationType.NEW_FRIENSHIP_REQUEST);
		this.userName = userName;
		this.toUserID = toUserID;
		params.put(ParameterName.USER_ID, fromUser);
	}

	public NotifNouvelleDemandeAmi(int id, int owner, String text, Timestamp creationTime, boolean isUnread, Timestamp readOn, Map<ParameterName, Object> parameters) {
		super(NotificationType.NEW_FRIENSHIP_REQUEST, id, owner, text, parameters, creationTime, isUnread, readOn);
	}

	@Override
	public String getTextToInsert() {
		final String link = MessageFormat.format("<a href=\"protected/afficher_reseau?id={0}\">Voir les demandes en cours</a>", toUserID);
		return MessageFormat.format("{0} vous a envoyé une demande d''ami ! {1}.", userName, link);
	}

}
