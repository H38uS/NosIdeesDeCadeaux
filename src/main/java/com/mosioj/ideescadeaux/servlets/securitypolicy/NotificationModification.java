package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class NotificationModification extends SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(NotificationModification.class);

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
	private boolean canModifyNotification(HttpServletRequest request, HttpServletResponse response) throws SQLException {

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
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canModifyNotification(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canModifyNotification(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	/**
	 * 
	 * @return The notification id, or null if the checks fail.
	 */
	public Integer getNotificationId() {
		return notificationId;
	}

}
