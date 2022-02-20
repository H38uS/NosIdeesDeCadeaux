package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/ajouter_idee_ami")
public class AjouterIdeeAmi extends IdeesCadeauxGetServlet<NetworkAccess> {

    private static final long serialVersionUID = -7053283110787519597L;
    public static final String USER_PARAMETER = "id";
    public static final String VIEW_PAGE_URL = "/protected/ajouter_idee_ami.jsp";

    public AjouterIdeeAmi() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        User user = policy.getUser();
        request.setAttribute("user", user);
        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritiesRepository.getPriorities());

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
