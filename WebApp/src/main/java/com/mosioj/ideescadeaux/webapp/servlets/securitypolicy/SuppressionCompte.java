package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuppressionCompte extends SecurityPolicy {

    private static final Logger logger = LogManager.getLogger(SuppressionCompte.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String userParameter;

    private User user;

    public SuppressionCompte(String userParameter) {
        this.userParameter = userParameter;
    }

    protected boolean canInteract(HttpServletRequest request) throws SQLException {

        if (!request.isUserInRole("ROLE_ADMIN")) {
            lastReason = "Non, mais non.";
            return false;
        }

        Optional<Integer> userId = ParametersUtils.readInt(request, userParameter);
        if (!userId.isPresent()) {
            lastReason = "Le paramètre est manquant.";
            return false;
        }

        user = UsersRepository.getUser(userId.get()).orElseThrow(SQLException::new);
        return true;
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteract(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteract(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    /**
     * @return The user to delete, or null if the checks have not passed.
     */
    public User getUserToDelete() {
        return user;
    }

    @Override
    public void reset() {
        user = null;
    }

}
