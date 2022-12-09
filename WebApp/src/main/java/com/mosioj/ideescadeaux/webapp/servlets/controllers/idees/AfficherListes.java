package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";
    public static final String AFFICHER_LISTES = "protected/afficher_listes";
    protected static final String NAME_OR_EMAIL = "name";

    /**
     * Class constructor.
     */
    public AfficherListes() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException, IOException {
        request.setAttribute("call_back", AFFICHER_LISTES);
        // Do not escape it as it is sent by the JS... And so escaped here
        String nameOrEmail = ParametersUtils.getGETParameterAsString(request, NAME_OR_EMAIL);
        final String fullURL = AFFICHER_LISTES + "?" + NAME_OR_EMAIL + "=" + nameOrEmail;
        request.setAttribute("identic_call_back", fullURL);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }
}
