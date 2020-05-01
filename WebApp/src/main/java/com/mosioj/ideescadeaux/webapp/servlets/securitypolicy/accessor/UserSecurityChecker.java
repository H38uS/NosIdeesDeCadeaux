package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor;

import com.mosioj.ideescadeaux.core.model.entities.User;

/**
 * Defines a class that aims to validate a comment parameter from a security point of view.
 *
 * @author Jordan Mosio
 */
public interface UserSecurityChecker {

    /**
     * @return The object if all validity checks passed, or null.
     */
    User getUser();
}
