package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A basic policy that allow all connection to the POST URL.
 *
 * @author Jordan Mosio
 */
public final class AllAccessToPostAndGet extends SecurityPolicy {

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public void reset() {
        // Nothing to do
    }

}
