package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    /** Display page. */
    private static final String FORM_URL = "/protected/rechercher_personne.jsp";

    /**
     * Class constructor.
     */
    public RechercherPersonne() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        String val = ParametersUtils.getGETParameterAsString(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
        request.setAttribute("onlyNonFriend", onlyNonFriend);
        RootingsUtils.rootToPage(FORM_URL, request, response);
    }


}
