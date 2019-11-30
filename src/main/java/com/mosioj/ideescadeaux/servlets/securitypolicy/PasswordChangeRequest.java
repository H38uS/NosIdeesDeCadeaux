package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.model.table.UserChangePwdRequest;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public final class PasswordChangeRequest extends SecurityPolicy implements UserSecurityChecker {

	private static final Logger logger = LogManager.getLogger(PasswordChangeRequest.class);

	/**
	 * Defines the string used in HttpServletRequest to retrieve the token id.
	 */
	private final String tokenParameter;

	/**
	 * Defines the string used in HttpServletRequest to retrieve the user id.
	 */
	private final String userIdParameter;

	private final UserChangePwdRequest ucpr;

	private User user;
	private Integer tokenId;

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

		Optional<Integer> userId = readInt(request, userIdParameter);
		Optional<Integer> tokenIdParam = readInt(request, tokenParameter);

		if (!userId.isPresent() || !tokenIdParam.isPresent()) {
			lastReason = "Aucune demande trouvée pour cet utilisateur.";
			return false;
		}

		if (!ucpr.isAValidCombinaison(userId.get(), tokenIdParam.get())) {
			lastReason = "Aucune demande trouvée pour cet utilisateur.";
			return false;
		}

		user = model.users.getUser(userId.get());
		tokenId = tokenIdParam.get();
		
		return true;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return isUserIdTokenValid(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return isUserIdTokenValid(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public User getUser() {
		return user;
	}

	/**
	 * 
	 * @return The valid token id in case the checks have succeeded.
	 */
	public Integer getTokenId() {
		return tokenId;
	}
}
