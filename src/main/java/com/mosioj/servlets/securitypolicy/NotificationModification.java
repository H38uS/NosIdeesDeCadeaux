package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Notification;
import com.mosioj.model.table.Notifications;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class NotificationModification extends AllAccessToPostAndGet implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the notification id.
	 */
	private final String notifParameter;

	private final Notifications notif;

	/**
	 * 
	 * @param notif
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the notification id.
	 */
	public NotificationModification(Notifications notif, String notifParameter) {
		this.notif = notif;
		this.notifParameter = notifParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 */
	private boolean canModifyNotification(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Integer notifId = ParametersUtils.readInt(request, notifParameter);
		if (notifId == null) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		Notification notification = notif.getNotification(notifId);
		if (notification == null) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		boolean res = userId == notification.getOwner();
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos notifications.";
		}
		return res;

	}

	// TODO : pouvoir directement accéder à la notification

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyNotification(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyNotification(request, response);
	}

}
