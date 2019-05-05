package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserChangePwdRequest;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class PasswordChangeRequest extends AllAccessToPostAndGet {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the token id.
	 */
	private final String tokenParameter;

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userIdParameter;

	private final UserChangePwdRequest ucpr;

	/**
	 * 
	 * @param ucpr
	 * @param tokenParameter Defines the string used in HttpServletRequest to retrieve the token id.
	 * @param userIdParameter Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	public PasswordChangeRequest(UserChangePwdRequest ucpr, String tokenParameter, String userIdParameter) {
		this.ucpr = ucpr;
		this.tokenParameter = tokenParameter;
		this.userIdParameter = userIdParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 */
	private boolean isUserIdTokenValid(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Optional<Integer> userId = ParametersUtils.readInt(request, userIdParameter);
		Optional<Integer> tokenId = ParametersUtils.readInt(request, tokenParameter);

		if (!userId.isPresent() || !tokenId.isPresent()) {
			lastReason = "Aucune demande trouvée pour cet utilisateur.";
			return false;
		}

		if (!ucpr.isAValidCombinaison(userId.get(), tokenId.get())) {
			lastReason = "Aucune demande trouvée pour cet utilisateur.";
			return false;
		}

		return true;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return isUserIdTokenValid(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return isUserIdTokenValid(request, response);
	}

}
