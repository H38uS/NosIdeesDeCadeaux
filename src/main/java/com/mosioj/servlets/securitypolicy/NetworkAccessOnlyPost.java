package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyPost;
import com.mosioj.utils.NotLoggedInException;

public final class NetworkAccessOnlyPost extends SecurityPolicyOnlyPost implements UserSecurityChecker {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;

	private User friend;

	/**
	 * 
	 * @param userParameter
	 */
	public NetworkAccessOnlyPost(String userParameter) {
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {
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
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

	@Override
	public User getUser() {
		return friend;
	}

}
