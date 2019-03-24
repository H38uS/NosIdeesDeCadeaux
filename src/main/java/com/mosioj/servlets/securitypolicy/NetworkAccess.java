package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class NetworkAccess extends AllAccessToPostAndGet implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;

	private final UserRelations userRelations;

	/**
	 * 
	 * @param userRelations
	 * @param userParameter
	 */
	public NetworkAccess(UserRelations userRelations, String userParameter) {
		this.userRelations = userRelations;
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {
		Integer user = ParametersUtils.readInt(request, userParameter);
		if (user == null) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getConnectedUser(request).id;
		boolean res = user == userId || userRelations.associationExists(user, userId);
		if (!res) {
			lastReason = "Vous n'êtes pas ami avec cette personne.";
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
