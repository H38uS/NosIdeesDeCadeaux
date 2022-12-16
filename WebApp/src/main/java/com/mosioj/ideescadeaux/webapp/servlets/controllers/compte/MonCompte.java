package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserParameter;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/mon_compte")
public class MonCompte extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(MonCompte.class);

    public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
    public static final String URL = "/protected/mon_compte";

    public MonCompte() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

        logger.debug("Displaying mon compte page...");
        User current = thisOne;
        request.setAttribute("user", current);

        List<UserParameter> userNotificationParameters = UserParametersRepository.getUserNotificationParameters(current);
        request.setAttribute("notif_types", userNotificationParameters);

        request.setAttribute("parents", ParentRelationshipRepository.getParents(current.id));
        request.setAttribute("children", ParentRelationshipRepository.getChildren(current));

        request.setAttribute("possible_values", NotificationActivation.values());
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
    }
}
