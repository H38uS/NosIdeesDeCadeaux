package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public final class NetworkAccess extends SecurityPolicy implements UserSecurityChecker {

    private static final Logger logger = LogManager.getLogger(NetworkAccess.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String userParameter;

    private User friend;

    /**
     * @param userParameter The request parameter name.
     */
    public NetworkAccess(String userParameter) {
        this.userParameter = userParameter;
    }

    private boolean hasAccess(HttpServletRequest request) throws SQLException {

        friend = ParametersUtils.readInt(request, userParameter).flatMap(UsersRepository::getUser).orElse(null);
        if (friend == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        final boolean isMe = connectedUser.equals(friend);
        final boolean res = isMe || UserRelationsRepository.associationExists(friend.id, connectedUser.id);
        if (!res) {
            lastReason = "Vous n'êtes pas ami avec cette personne.";
            return false;
        }

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

    @Override
    public void reset() {
        friend = null;
    }

}
