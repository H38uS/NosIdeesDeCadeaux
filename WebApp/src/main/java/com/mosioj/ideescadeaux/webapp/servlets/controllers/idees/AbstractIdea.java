package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;

public abstract class AbstractIdea<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    // FIXME : 0 faire une passe et supprimer tout ceux qui hérite et qui sont faits en service ?
    private static final long serialVersionUID = -1774633803227715931L;

    /**
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public AbstractIdea(P policy) {
        super(policy);
    }

}
