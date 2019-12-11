package com.mosioj.ideescadeaux.servlets.rootservlet;

import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxGetServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    private static final long serialVersionUID = -1513319177739695079L;

    public IdeesCadeauxGetServlet(P policy) {
        super(policy);
    }

    @Override
    public final void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        throw new ServletException("Method not supported");
    }

}
