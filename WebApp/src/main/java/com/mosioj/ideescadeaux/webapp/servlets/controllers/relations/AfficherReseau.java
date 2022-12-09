package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends IdeesCadeauxGetServlet<NetworkAccess> {

    public static final String USER_ID_PARAM = "id";
    public static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

    /** Class constructor. */
    public AfficherReseau() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        User friend = policy.getUser();

        request.setAttribute("id", friend.id);
        request.setAttribute("name", friend.getMyDName());
        request.setAttribute("looking_for", ParametersUtils.getGETParameterAsString(request, "looking_for"));

        RootingsUtils.rootToPage(DISPATCH_URL, request, response);
    }

}
