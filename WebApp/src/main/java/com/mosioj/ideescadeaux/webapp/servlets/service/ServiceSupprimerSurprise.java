package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.SurpriseModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/service/supprimer_surprise")
public class ServiceSupprimerSurprise extends ServicePost<SurpriseModification> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceSupprimerSurprise.class);

    /** Service parameter. */
    protected static final String IDEA_ID_PARAM = "idee";

    public ServiceSupprimerSurprise() {
        super(new SurpriseModification(IDEA_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Idee idea = policy.getIdea();
        logger.debug(MessageFormat.format("Suppression de la surprise {0} par {1}.", idea.getId(), thisOne));
        IdeesRepository.remove(idea);
        buildResponse(response, ServiceResponse.ok("La surprise a bien été supprimée.", thisOne));
    }
}
