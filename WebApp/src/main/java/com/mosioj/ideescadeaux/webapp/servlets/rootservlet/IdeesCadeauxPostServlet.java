package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxPostServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeesCadeauxPostServlet.class);

    public IdeesCadeauxPostServlet(P policy) {
        super(policy);
    }

    @Override
    public final void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        logger.warn("Unsupported GET access: {}", request.getRequestURL().toString());
    }

}
