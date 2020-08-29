package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/sous_reserver")
public class SousReserverIdee extends IdeesCadeauxGetServlet<IdeaInteraction> {

    private static final String IDEA_ID_PARAM = "idee";
    public static final String URL = "/protected/sous_reserver";
    public static final String VIEW_PAGE_URL = "/protected/sous_reservation.jsp";

    /** Class constructor */
    public SousReserverIdee() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        req.setAttribute("idee", idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
    }
}
