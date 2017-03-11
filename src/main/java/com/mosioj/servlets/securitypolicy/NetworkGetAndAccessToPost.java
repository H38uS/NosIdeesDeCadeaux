package com.mosioj.servlets.securitypolicy;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

public class NetworkGetAndAccessToPost implements SecurityPolicy {

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
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {

		Integer user = ParametersUtils.readInt(request, userParameter);
		if (user == null) {
			return false;
		}

		try {
			int userId = ParametersUtils.getUserId(request);
			if (user != userId && !userRelations.associationExists(user, userId)) {
				return false;
			}
		} catch (SQLException e) {
			try {
				RootingsUtils.rootToGenericSQLError(e, request, response);
			} catch (ServletException | IOException e1) {
				// Nothing to do
			}
			return false;
		}

		return true;
	}

	@Override
	public boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		return true;
	}

	@Override
	public boolean isPostRequestAllowed() {
		return true;
	}

}
