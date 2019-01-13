package com.mosioj.servlets.logichelpers;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import com.mosioj.model.User;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.Users;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class NetworkInteractions {

	public static final String USER_ID_PARAM = "user_id";

	private final Notifications notif = new Notifications();
	private final Users users = new Users();
	private final UserRelationRequests userRelationRequests = new UserRelationRequests();
	private final UserRelations userRelations = new UserRelations();

	/**
	 * 
	 * @param request
	 * @throws SQLException
	 * @throws NotLoggedInException
	 */
	public void sendARequest(HttpServletRequest request) throws SQLException, NotLoggedInException {

		User userToSendInvitation = users.getUser(ParametersUtils.readInt(request, USER_ID_PARAM));
		int userId = ParametersUtils.getUserId(request);
		request.setAttribute("name", userToSendInvitation.getName());

		if (userRelationRequests.associationExists(userId, userToSendInvitation.id)) {
			throw new SQLException(MessageFormat.format("vous avez déjà envoyé une demande à {0}.", userToSendInvitation.getName()));
		}
		
		if (userRelations.associationExists(userId, userToSendInvitation.id)) {
			throw new SQLException(MessageFormat.format("vous êtes déjà ami avec {0}.", userToSendInvitation.getName()));
		}

		// Suppression des notifications
		notif.removeAllType(userId, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userToSendInvitation.id);
		notif.removeAllType(userToSendInvitation.id, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userId);

		// On ajoute l'association
		userRelationRequests.insert(userId, userToSendInvitation.id);
		notif.addNotification(	userToSendInvitation.id,
								new NotifNouvelleDemandeAmi(userId, userToSendInvitation.id, users.getUser(userId).getName()));
	}

	/**
	 * 
	 * @param currentId The one that triggers the relation removal.
	 * @param otherOne The relationship being dropped.
	 * @throws SQLException 
	 */
	public void deleteRelationship(int currentId, int otherOne) throws SQLException {
		userRelations.deleteAssociation(otherOne, currentId);
		notif.removeAllType(currentId, NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, otherOne);
		notif.removeAllType(otherOne, NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, currentId);
		
		// Send a notification
		User me = users.getUser(currentId);
		notif.addNotification(otherOne, new NotifFriendshipDropped(currentId, me.getName()));
	}

}
