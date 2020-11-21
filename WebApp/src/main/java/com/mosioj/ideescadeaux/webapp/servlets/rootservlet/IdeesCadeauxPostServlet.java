package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxPostServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeesCadeauxPostServlet.class);

    /** Not found view. */
    private static final String VIEW_PUBLIC_URL = "/public/NotFound.jsp";

    public IdeesCadeauxPostServlet(P policy) {
        super(policy);
    }

    @Override
    public final void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        logger.warn("Unsupported GET access: {}", request.getRequestURL().toString());
        RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
    }

}
