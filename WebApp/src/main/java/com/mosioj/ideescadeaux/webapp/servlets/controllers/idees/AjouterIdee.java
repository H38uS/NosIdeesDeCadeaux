package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/ajouter_idee")
public class AjouterIdee extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    public static final String VIEW_PAGE_URL = "/protected/completer_ma_liste.jsp";

    /**
     * Class constructor.
     */
    public AjouterIdee() {
        // No security : we will see only our content.
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse resp) throws ServletException, SQLException {
        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritiesRepository.getPriorities());
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
    }

}
