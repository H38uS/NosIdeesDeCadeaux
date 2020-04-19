package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ChildAdministration extends SecurityPolicy implements UserSecurityChecker {

    private static final Logger logger = LogManager.getLogger(ChildAdministration.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String childParameter;

    private User user;

    /**
     * @param childParameter Request parameter name.
     */
    public ChildAdministration(String childParameter) {
        this.childParameter = childParameter;
    }

    private boolean hasAccess(HttpServletRequest request) throws SQLException {

        Optional<Integer> child = ParametersUtils.readInt(request, childParameter);
        if (!child.isPresent()) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        int userId = connectedUser.id;
        final boolean isAdmin = IdeesCadeauxServlet.isAdmin(request);
        if (ParentRelationshipRepository.noRelationExists(userId, child.get()) && !isAdmin) {
            lastReason = "Vous n'êtes pas un parent de cette personne...";
            return false;
        }

        user = UsersRepository.getUser(child.get()).orElseThrow(SQLException::new);
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

    @Override
    public void reset() {
        user = null;
    }

}
