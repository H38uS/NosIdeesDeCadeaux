package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.utils.NotLoggedInException;

public class NameServicePolicy extends SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;

	private User user;

	public NameServicePolicy(String userParameter) {
		this.userParameter = userParameter;
	}

	@Override
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {

		int userId = readInt(request, userParameter).orElse(connectedUser.id);
		if (userId != connectedUser.id && !model.userRelations.associationExists(userId, connectedUser.id)) {
			// On regarde
			// Soit son propre réseau
			// Soit celui d'un ami
			userId = connectedUser.id;
		}

		user = model.users.getUser(userId);

		return true;
	}

	@Override
	public boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return false;
	}

	@Override
	public boolean isPostRequestAllowed() {
		return false;
	}

	/**
	 * 
	 * @return The user of which to fetch the network names.
	 */
	public User getRootNetwork() {
		return user;
	}

}
