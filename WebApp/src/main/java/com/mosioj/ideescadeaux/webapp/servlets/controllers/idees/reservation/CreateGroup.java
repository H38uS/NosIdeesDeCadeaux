package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/create_a_group")
public class CreateGroup extends IdeesCadeauxGetServlet<IdeaInteraction> {

    /** The parameter read to link the future group to the idea. */
    private static final String IDEE_FIELD_PARAMETER = "idee";

    /** The view */
    public static final String VIEW_PAGE_URL = "/protected/create_a_group.jsp";

    /**
     * Class contructor
     */
    public CreateGroup() {
        super(new IdeaInteraction(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        req.setAttribute("idee", idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
    }

}
