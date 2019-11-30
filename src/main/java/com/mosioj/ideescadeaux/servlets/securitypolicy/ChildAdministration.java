package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

public final class ChildAdministration extends SecurityPolicy implements UserSecurityChecker {

	private static final Logger logger = LogManager.getLogger(ChildAdministration.class);

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String childParameter;

	private User user;

	/**
	 * 
	 * @param childParameter
	 */
	public ChildAdministration(String childParameter) {
		this.childParameter = childParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException {

		Optional<Integer> child = readInt(request, childParameter);
		if (!child.isPresent()) {
			lastReason = "Aucun utilisateur trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;
		if (!model.parentRelationship.doesRelationExists(userId, child.get())) {
			lastReason = "Vous n'êtes pas un parent de cette personne...";
			return false;
		}

		user = model.users.getUser(child.get());
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
		return user;
	}

}
