package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxGetServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeesCadeauxGetServlet.class);

    public IdeesCadeauxGetServlet(P policy) {
        super(policy);
    }

    @Override
    // FIXME : 0 regarder tous les services qui peuvent avoir plusieurs pages et les gérer dans le JS
    public final void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.warn("Unsupported POST access: {}", request.getRequestURL().toString());
        throw new ServletException("Unsupported method POST");
    }

}
