package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.ChildAdministration;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/connexion_enfant")
public class ConnexionEnfant extends IdeesCadeauxPostServlet<ChildAdministration> {

    private static final long serialVersionUID = 7598797241503497392L;
    private static final Logger logger = LogManager.getLogger(ConnexionEnfant.class);
    private static final String CHILD_ID_PARAM = "name";
    public static final String VIEW_PAGE_URL = "/protected/child_success.jsp";

    public ConnexionEnfant() {
        super(new ChildAdministration(CHILD_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        if (request.getAttribute("initial_connected_user") != null) {
            request.setAttribute("error_message",
                                 "Vous vous êtes déjà connecté à un autre utilisateur. Revenez d'abord à votre compte.");
            RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
            return;
        }

        User current = thisOne;
        User newOne = policy.getUser();

        logger.info(MessageFormat.format("Connection depuis {0} en tant que {1}.", current.id, newOne.getId()));
        HttpSession session = request.getSession();

        session.setAttribute("connected_user", newOne);
        request.setAttribute("connected_user", newOne);
        request.setAttribute("new_name", newOne.getName());

        session.setAttribute("initial_connected_user", current);
        request.setAttribute("initial_connected_user", current);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
