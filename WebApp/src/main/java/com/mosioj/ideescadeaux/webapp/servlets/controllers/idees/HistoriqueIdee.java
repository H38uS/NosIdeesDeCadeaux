package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/idee/historique")
public class HistoriqueIdee extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    /** The page to display. */
    private static final String VIEW_PAGE_URL = "/protected/idea_history.jsp";

    /** The call back url. */
    public static final String PROTECTED_IDEE_HISTORIQUE = "protected/idee/historique";

    /** Class contructor */
    public HistoriqueIdee() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        request.setAttribute("call_back", PROTECTED_IDEE_HISTORIQUE);
        request.setAttribute("identic_call_back", PROTECTED_IDEE_HISTORIQUE);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }
}
