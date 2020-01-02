package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        Optional<Integer> user = ParametersUtils.readInt(request, userParameter);
        if (!user.isPresent()) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        int userId = connectedUser.id;
        boolean res = user.get() == userId || UserRelationsRepository.associationExists(user.get(), userId);
        if (!res) {
            lastReason = "Vous n'êtes pas ami avec cette personne.";
            return false;
        }

        friend = UsersRepository.getUser(user.get()).orElseThrow(SQLException::new);
        if (friend == null) {
            logger.error("The id " + user.get() + " does not exist...");
        }

        return friend != null;
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
