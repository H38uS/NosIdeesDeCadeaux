package com.mosioj.ideescadeaux.webapp.servlets.service;


import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/service/ajouter_idee")
public class ServiceAjouterIdee extends AbstractIdea<AllAccessToPostAndGet> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(AjouterIdee.class);

    public ServiceAjouterIdee() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        // nothing to do on get
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

        ServiceResponse<String> sr = ServiceResponse.ok("L'idée a bien été crée sur le serveur.", isAdmin(request),
                                                        thisOne);
        try {
            // Check that we have a file upload request
            if (ServletFileUpload.isMultipartContent(request)) {

                fillIdeaOrErrors(request, response);

                if (!errors.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    message.append("Des erreurs ont empêché la sauvegarde de cette idée");
                    message.append("<ul>");
                    errors.forEach(e -> message.append("<li>").append(e).append("</li>"));
                    message.append("</ul>");
                    sr = ServiceResponse.ko(message.toString(), isAdmin(request), thisOne);
                } else {
                    logger.info(MessageFormat.format("Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     parameters.get("priority")));
                    int ideaId = IdeesRepository.addIdea(thisOne,
                                                         parameters.get("text"),
                                                         parameters.get("type"),
                                                         Integer.parseInt(parameters.get("priority")),
                                                         parameters.get("image"),
                                                         null,
                                                         thisOne);
                    IdeesRepository.getIdeaWithoutEnrichment(ideaId).ifPresent(
                            i -> {
                                try {
                                    addModificationNotification(thisOne, i, true);
                                } catch (SQLException e) {
                                    logger.error(e);
                                }
                            }
                    );
                    NotificationsRepository.removeAllType(thisOne, NotificationType.NO_IDEA);
                }
            }
        } catch (ServletException | IOException | SQLException e) {
            logger.error(e);
            sr = ServiceResponse.ko("Une erreur est survenue lors de l'ajout de cette idée: " + e.getMessage(),
                                    isAdmin(request), thisOne);
        }

        buildResponse(response, sr);
    }
}
