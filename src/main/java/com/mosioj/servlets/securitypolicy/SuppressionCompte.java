package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyPost;
import com.mosioj.utils.NotLoggedInException;

public class SuppressionCompte extends SecurityPolicyOnlyPost {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userParameter;
	
	private User user;
	
	public SuppressionCompte(String userParameter) {
		this.userParameter = userParameter;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		
		if (!request.isUserInRole("ROLE_ADMIN")) {
			lastReason = "Non, mais non.";
			return false;
		}
		
		Optional<Integer> userId = readInt(request, userParameter);
		if (!userId.isPresent()) {
			lastReason = "Le paramètre est manquant.";
			return false;
		}
		
		user = model.users.getUser(userId.get());
		
		return false;
	}

	/**
	 * 
	 * @return The user to delete, or null if the checks have not passed.
	 */
	public User getUserToDelete() {
		return user;
	}
}
