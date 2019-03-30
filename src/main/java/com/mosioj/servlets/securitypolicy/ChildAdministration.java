package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class ChildAdministration extends AllAccessToPostAndGet {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String childParameter;

	/**
	 * 
	 * @param childParameter
	 */
	public ChildAdministration(String childParameter) {
		this.childParameter = childParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {

		Integer child = ParametersUtils.readInt(request, childParameter);
		if (child == null) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;
		boolean res = model.parentRelationship.doesRelationExists(userId, child);
		if (!res) {
			lastReason = "Vous n'êtes pas un parent de cette personne...";
		}
		return res;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

}
