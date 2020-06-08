package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxGetServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    private static final long serialVersionUID = -1513319177739695079L;

    public IdeesCadeauxGetServlet(P policy) {
        super(policy);
    }

    @Override
    // FIXME : 0 regarder tous les services qui peuvent avoir plusieurs pages et les gérer dans le JS
    // FIXME : 0 faire une couche intermediaire pour les services pour gérer les exceptions SQL et envoyer une réponse KO
    public final void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        throw new ServletException("Method not supported");
    }

}
