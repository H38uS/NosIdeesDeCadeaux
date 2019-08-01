package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;

public final class NameServicePolicy extends SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(NameServicePolicy.class);

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;

	private User user;

	public NameServicePolicy(String userParameter) {
		this.userParameter = userParameter;
	}

	protected boolean hasRight(HttpServletRequest request) throws SQLException {
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
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {

		try {
			return hasRight(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return hasRight(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	/**
	 * 
	 * @return The user of which to fetch the network names.
	 */
	public User getRootNetwork() {
		return user;
	}

}
