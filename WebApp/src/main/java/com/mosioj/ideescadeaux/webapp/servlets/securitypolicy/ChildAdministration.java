package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public final class ChildAdministration extends SecurityPolicy implements UserSecurityChecker {

    private static final Logger logger = LogManager.getLogger(ChildAdministration.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String childParameter;

    private User child;

    /**
     * @param childParameter Request parameter name.
     */
    public ChildAdministration(String childParameter) {
        this.childParameter = childParameter;
    }

    private boolean hasAccess(HttpServletRequest request) throws SQLException {

        child = ParametersUtils.readInt(request, childParameter).flatMap(UsersRepository::getUser).orElse(null);
        if (child == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        final boolean isAdmin = connectedUser.isAdmin();
        if (ParentRelationshipRepository.noRelationExists(connectedUser.id, child.id) && !isAdmin) {
            lastReason = "Vous n'êtes pas un parent de cette personne...";
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
        return child;
    }

    @Override
    public void reset() {
        child = null;
    }

}
