package com.mosioj.ideescadeaux.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.ideescadeaux.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.entities.UserParameter;
import com.mosioj.ideescadeaux.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/mon_compte")
public class MonCompte extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = -101081965549681889L;
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

        HttpSession session = request.getSession();
        if (session.getAttribute("sauvegarde_ok") != null) {
            request.setAttribute("sauvegarde_ok", session.getAttribute("sauvegarde_ok"));
            session.removeAttribute("sauvegarde_ok");
        }
        if (session.getAttribute("errors_info_gen") != null) {
            request.setAttribute("errors_info_gen", session.getAttribute("errors_info_gen"));
            session.removeAttribute("errors_info_gen");
        }

        List<UserParameter> userNotificationParameters = UserParametersRepository.getUserNotificationParameters(current.id);
        request.setAttribute("notif_types", userNotificationParameters);

        request.setAttribute("parents", ParentRelationshipRepository.getParents(current.id));
        request.setAttribute("children", ParentRelationshipRepository.getChildren(current.id));

        request.setAttribute("possible_values", NotificationActivation.values());
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
    }
}
