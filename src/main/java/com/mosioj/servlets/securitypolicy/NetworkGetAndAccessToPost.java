package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.ParametersUtils;

public class NetworkGetAndAccessToPost extends AllAccessToPostAndGet implements SecurityPolicy {

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
	public NetworkGetAndAccessToPost(UserRelations userRelations, String userParameter) {
		this.userRelations = userRelations;
		this.userParameter = userParameter;
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Integer user = ParametersUtils.readInt(request, userParameter);
		if (user == null) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);
		boolean res = user != userId || !userRelations.associationExists(user, userId);
		if (!res) {
			lastReason = "Vous n'avez pas accès au réseau de cette personne.";
		}
		return res;
	}

}
