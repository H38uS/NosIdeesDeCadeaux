package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public final class NameServicePolicy extends SecurityPolicy {

    private static final Logger logger = LogManager.getLogger(NameServicePolicy.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String userParameter;

    private User user;

    public NameServicePolicy(String userParameter) {
        this.userParameter = userParameter;
    }

    private boolean hasRight(HttpServletRequest request) throws SQLException {

        user = ParametersUtils.readInt(request, userParameter).flatMap(UsersRepository::getUser).orElse(connectedUser);

        if (!user.equals(connectedUser) && !UserRelationsRepository.associationExists(user, connectedUser)) {
            // On regarde
            // Soit son propre réseau
            // Soit celui d'un ami
            // On force pour ne pas avoir d'exception
            user = connectedUser;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return hasRight(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return hasRight(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    /**
     * @return The user of which to fetch the network names.
     */
    public User getRootNetwork() {
        return user;
    }

    @Override
    public void reset() {
        user = null;
    }

}
