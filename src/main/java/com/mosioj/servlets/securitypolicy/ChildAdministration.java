package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.utils.NotLoggedInException;

public class ChildAdministration extends AllAccessToPostAndGet implements UserSecurityChecker {

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

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {

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
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

	@Override
	public User getUser() {
		return user;
	}

}
