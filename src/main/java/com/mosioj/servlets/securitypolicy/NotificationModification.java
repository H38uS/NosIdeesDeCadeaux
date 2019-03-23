package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.ParentRelationship;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.utils.NotLoggedInException;
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
	 * @throws NotLoggedInException
	 */
	private boolean canModifyNotification(	HttpServletRequest request,
											HttpServletResponse response) throws SQLException, NotLoggedInException {

		Integer notifId = ParametersUtils.readInt(request, notifParameter);
		if (notifId == null) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		AbstractNotification n = notif.getNotification(notifId);
		if (n == null) {
			lastReason = "Aucune notification trouvée en paramètre.";
			return false;
		}

		boolean res = userId == n.getOwner() || new ParentRelationship().getChildren(userId).contains(new User(n.getOwner(), "", "", ""));
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos notifications ou celles de vos enfants.";
		}
		return res;

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

}
