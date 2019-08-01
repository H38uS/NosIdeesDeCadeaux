package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;

public final class NetworkAccess extends SecurityPolicy implements UserSecurityChecker {

	private static final Logger logger = LogManager.getLogger(NetworkAccess.class);

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;

	private User friend;

	/**
	 * 
	 * @param userParameter
	 */
	public NetworkAccess(String userParameter) {
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException {
		Optional<Integer> user = readInt(request, userParameter);
		if (!user.isPresent()) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;
		boolean res = user.get() == userId || model.userRelations.associationExists(user.get(), userId);
		if (!res) {
			lastReason = "Vous n'êtes pas ami avec cette personne.";
			return false;
		}

		friend = model.users.getUser(user.get());
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return hasAccess(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return hasAccess(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public User getUser() {
		return friend;
	}

}
