package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaInteractions;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
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

@WebServlet("/protected/service/modifier_idee")
public class ServiceModifierIdee extends AbstractIdea<IdeaModification> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceModifierIdee.class);

    public static final String IDEE_ID_PARAM = "id";

    public ServiceModifierIdee() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        // nothing to do on get
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

        Idee idea = policy.getIdea();
        ServiceResponse<?> sr = ServiceResponse.ok(idea.getId(), isAdmin(request), thisOne);

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
                    logger.info(MessageFormat.format("Modifying an idea [''{0}'' / ''{1}'' / ''{2}'']",
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     parameters.get("priority")));
                    String image = parameters.get("image");
                    String old = parameters.get("old_picture");
                    logger.debug("Image précédente: " + old + " / Nouvelle image: " + image);
                    if (image == null || image.isEmpty() || "null".equals(image)) {
                        if (old != null && !old.equals("undefined")) {
                            image = old;
                        } else {
                            image = null;
                        }
                    } else {
                        // Modification de l'image
                        // On supprime la précédente
                        if (!"default.png".equals(old)) {
                            IdeaInteractions helper = new IdeaInteractions();
                            helper.removeUploadedImage(ParametersUtils.getIdeaPicturePath(), old);
                        }
                        logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
                    }

                    IdeesRepository.modifier(idea.getId(),
                                             parameters.get("text"),
                                             parameters.get("type"),
                                             parameters.get("priority"),
                                             image);
                    User user = thisOne;

                    // Ajout de notification aux amis si l'anniversaire approche
                    addModificationNotification(user, policy.getIdea(), false);

                    // Suppression des demandes si y'en avait
                    IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

                    NotificationsRepository.getNotification(ParameterName.IDEA_ID, idea.getId()).forEach(n -> {
                        if (n instanceof NotifAskIfIsUpToDate) {
                            NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) n;
                            NotificationsRepository.addNotification(isUpToDate.getUserIdParam(),
                                                                    new NotifConfirmedUpToDate(user, idea));
                            NotificationsRepository.remove(n);
                        }
                        if (n instanceof NotifIdeaAddedByFriend) {
                            NotificationsRepository.remove(n);
                        }
                    });
                }
            }
        } catch (ServletException | IOException | SQLException e) {
            logger.error(e);
            sr = ServiceResponse.ko("Une erreur est survenue lors de la modification de cette idée: " + e.getMessage(),
                                    isAdmin(request), thisOne);
        }

        buildResponse(response, sr);
    }
}
