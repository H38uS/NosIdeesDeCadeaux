package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.controllers.idees.reservation.DereserverIdee;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;

@WebServlet("/protected/service/dereserver")
public class ServiceDereserver extends IdeesCadeauxPostServlet<IdeaInteraction> {

    private static final Logger logger = LogManager.getLogger(ServiceDereserver.class);
    private static final long serialVersionUID = -8244829899125982644L;
    private static final String IDEA_ID_PARAM = "idee";

    /**
     * Class constructor
     */
    public ServiceDereserver() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        int userId = thisOne.id;

        logger.debug(MessageFormat.format("Annulation de la réservation de l''idée {0} par {1}.",
                                          idea.getId(),
                                          userId));
        model.idees.dereserver(idea.getId(), userId);

        buildResponse(response, ServiceResponse.ok("", true, isAdmin(request)));
    }
}