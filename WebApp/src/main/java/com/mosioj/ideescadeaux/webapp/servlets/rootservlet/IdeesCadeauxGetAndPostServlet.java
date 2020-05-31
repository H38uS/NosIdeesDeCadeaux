package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class IdeesCadeauxGetAndPostServlet<P extends SecurityPolicy> extends IdeesCadeauxServlet<P> {

    /**
     * Class constructor.
     *
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public IdeesCadeauxGetAndPostServlet(P policy) {
        super(policy);
    }

    @Override
    protected void dealWithUnauthorizedPolicyAccess(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    P policy) {
        RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
    }
}
