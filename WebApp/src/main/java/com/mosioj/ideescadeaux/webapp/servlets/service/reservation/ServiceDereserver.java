package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/service/dereserver")
public class ServiceDereserver extends ServicePost<IdeaInteraction> {

    private static final Logger logger = LogManager.getLogger(ServiceDereserver.class);
    private static final long serialVersionUID = -8244829899125982644L;
    public static final String IDEA_ID_PARAM = "idee";

    /**
     * Class constructor
     */
    public ServiceDereserver() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        int userId = thisOne.id;

        logger.debug(MessageFormat.format("Annulation de la réservation de l''idée {0} par {1}.",
                                          idea.getId(),
                                          userId));
        IdeesRepository.dereserver(idea, thisOne);

        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
