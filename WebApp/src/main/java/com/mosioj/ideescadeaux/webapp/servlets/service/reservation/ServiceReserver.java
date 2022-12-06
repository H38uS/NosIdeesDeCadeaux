package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
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

@WebServlet("/protected/service/reserver")
public class ServiceReserver extends ServicePost<IdeaInteraction> {

    private static final String IDEA_ID_PARAM = "idee";

    private static final Logger logger = LogManager.getLogger(ServiceReserver.class);

    public ServiceReserver() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), thisOne));

        if (IdeesRepository.canBook(idea, thisOne)) {
            IdeesRepository.reserver(idea, thisOne);
            NotificationsRepository.terminator().whereType(NType.RECURENT_IDEA_UNBOOK).whereIdea(idea).terminates();
        }

        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
