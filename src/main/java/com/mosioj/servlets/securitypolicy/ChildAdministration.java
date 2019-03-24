package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.ParentRelationship;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class ChildAdministration extends AllAccessToPostAndGet implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String childParameter;

	private final ParentRelationship parentRelationship;

	/**
	 * 
	 * @param parentRelationship
	 * @param childParameter
	 */
	public ChildAdministration(ParentRelationship parentRelationship, String childParameter) {
		this.parentRelationship = parentRelationship;
		this.childParameter = childParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {

		Integer child = ParametersUtils.readInt(request, childParameter);
		if (child == null) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getConnectedUser(request).id;
		boolean res = parentRelationship.doesRelationExists(userId, child);
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
