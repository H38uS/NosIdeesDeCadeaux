package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserChangePwdRequestRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * A policy to make sure we can interact with an idea.
 *
 * @author Jordan Mosio
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

    private User user;
    private Integer tokenId;

    /**
     * @param tokenParameter  Defines the string used in HttpServletRequest to retrieve the token id.
     * @param userIdParameter Defines the string used in HttpServletRequest to retrieve the user id.
     */
    public PasswordChangeRequest(String tokenParameter, String userIdParameter) {
        this.tokenParameter = tokenParameter;
        this.userIdParameter = userIdParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean isUserIdTokenValid(HttpServletRequest request) throws SQLException {

        user = ParametersUtils.readInt(request, userIdParameter).flatMap(UsersRepository::getUser).orElse(null);
        if (user == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        Integer tokenId = ParametersUtils.readInt(request, tokenParameter).orElse(null);
        if (tokenId == null) {
            lastReason = "Aucune demande trouvée pour cet utilisateur.";
            return false;
        }

        if (!UserChangePwdRequestRepository.isAValidCombinaison(user.id, tokenId)) {
            lastReason = "Aucune demande trouvée pour cet utilisateur.";
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return isUserIdTokenValid(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return isUserIdTokenValid(request);
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
     * @return The valid token id in case the checks have succeeded.
     */
    public Integer getTokenId() {
        return tokenId;
    }

    @Override
    public void reset() {
        user = null;
        tokenId = null;
    }
}
