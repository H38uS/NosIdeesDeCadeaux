package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends IdeesCadeauxGetServlet<IdeaModification> {

    private static final long serialVersionUID = -1774633803227715931L;
    public static final String IDEE_ID_PARAM = "id";
    public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";

    /**
     * Class constructor.
     */
    public ModifyIdea() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();

        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritiesRepository.getPriorities());
        request.setAttribute("idea", idea);

        Object errors = request.getSession().getAttribute("errors");
        if (errors != null) {
            request.setAttribute("errors", errors);
            request.getSession().removeAttribute("errors");
        }

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
