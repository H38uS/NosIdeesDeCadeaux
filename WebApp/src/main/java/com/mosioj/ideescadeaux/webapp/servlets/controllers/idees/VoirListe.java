package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/protected/voir_liste")
public class VoirListe extends IdeesCadeauxGetServlet<NetworkAccess> {

    public static final String PROTECTED_VOIR_LISTE = "/protected/voir_liste";
    private static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";
    public static final String USER_ID_PARAM = "id";

    /**
     * Class constructor.
     */
    public VoirListe() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException, IOException {
        final String url = PROTECTED_VOIR_LISTE.substring(1);
        request.setAttribute("call_back", url);
        final String fullURL = url + "?" + USER_ID_PARAM + "=" + policy.getUser().id;
        request.setAttribute("identic_call_back", fullURL);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }
}
