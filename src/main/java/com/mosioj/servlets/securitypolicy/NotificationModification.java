package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyGetAndPost;
import com.mosioj.utils.NotLoggedInException;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class NotificationModification extends SecurityPolicyGetAndPost {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the notification id.
	 */
	private final String notifParameter;

	private Integer notificationId;

	/**
	 * 
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the notification id.
	 */
	public NotificationModification(String notifParameter) {
		this.notifParameter = notifParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 * @throws NotLoggedInException
	 */
	private boolean canModifyNotification(	HttpServletRequest request,
											HttpServletResponse response) throws SQLException, NotLoggedInException {

		Optional<Integer> notifId = readInt(request, notifParameter);
		if (!notifId.isPresent()) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		int userId = connectedUser.id;

		AbstractNotification n = model.notif.getNotification(notifId.get());
		if (n == null) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		boolean res = userId == n.getOwner()
				|| model.parentRelationship.getChildren(userId).contains(new User(n.getOwner(), "", "", ""));
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos notifications ou celles de vos enfants.";
			return false;
		}

		notificationId = notifId.get();

		return true;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyNotification(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyNotification(request, response);
	}

	/**
	 * 
	 * @return The notification id, or null if the checks fail.
	 */
	public Integer getNotificationId() {
		return notificationId;
	}
}
