package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.Idee;

@WebServlet("/protected/service/reserver")
public class ServiceReserver extends IdeesCadeauxPostServlet<IdeaInteraction> {

    private static final long serialVersionUID = 2642366164643542379L;
    private static final String IDEA_ID_PARAM = "idee";

    private static final Logger logger = LogManager.getLogger(ServiceReserver.class);

    public ServiceReserver() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), thisOne));

        if (IdeesRepository.canBook(idea.getId(), thisOne.id)) {
            IdeesRepository.reserver(idea.getId(), thisOne.id);
        }

        buildResponse(response, ServiceResponse.ok(isAdmin(request)));
    }
}
