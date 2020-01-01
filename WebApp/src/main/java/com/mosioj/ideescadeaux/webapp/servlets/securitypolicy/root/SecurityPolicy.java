package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.core.model.entities.User;

public abstract class SecurityPolicy {

    protected String lastReason = "";
    protected User connectedUser;

    /**
     * Sets up a new connected user. May be null.
     *
     * @param user The connected user.
     */
    public void setConnectedUser(User user) {
        connectedUser = user;
    }

    /**
     * Reset the
     */
    public abstract void reset();

    /**
     * @param request The http request.
     * @param response The http response.
     * @return True if and only if the current connected user can perform a Get request with embedded parameters.
     */
    public abstract boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response);

    /**
	 * @param request The http request.
	 * @param response The http response.
     * @return True if and only if the current connected user can perform a Get request with embedded parameters.
     */
    public abstract boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response);


    /**
     * @return The last reason for a forbidden access.
     */
    public String getLastReason() {
        return lastReason;
    }
}
