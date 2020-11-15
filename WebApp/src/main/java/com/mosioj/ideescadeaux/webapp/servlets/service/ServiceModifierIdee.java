package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.CONFIRMED_UP_TO_DATE;

@WebServlet("/protected/service/modifier_idee")
public class ServiceModifierIdee extends ServicePost<IdeaModification> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceModifierIdee.class);

    public static final String IDEE_ID_PARAM = "id";

    public ServiceModifierIdee() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        ServiceResponse<?> sr = ServiceResponse.ok(idea.getId(), isAdmin(request), thisOne);

        // Check that we have a file upload request
        if (ServletFileUpload.isMultipartContent(request)) {

            final File ideaPicturePath = ParametersUtils.getIdeaPicturePath();
            final Map<String, String> parameters = ParametersUtils.readMultiFormParameters(request, ideaPicturePath);
            List<String> errors = IdeaLogic.fillIdeaOrErrors(parameters);

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
                        IdeaLogic.removeUploadedImage(ParametersUtils.getIdeaPicturePath(), old);
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
                IdeaLogic.addModificationNotification(user, policy.getIdea(), false);

                // Suppression des demandes si y'en avait
                IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

                // Mise à jour des demandes de confirmations si à jour
                final Notification confirmationUpToDate = CONFIRMED_UP_TO_DATE.with(user, idea);
                NotificationsRepository.fetcher()
                                       .whereType(NType.IS_IDEA_UP_TO_DATE)
                                       .whereIdea(idea)
                                       .fetch()
                                       .forEach(n -> {
                                           n.getUserParameter().ifPresent(confirmationUpToDate::sendItTo);
                                           NotificationsRepository.remove(n);
                                       });

                // Suppression des notifications d'ajout par un amis.
                NotificationsRepository.terminator().whereType(NType.IDEA_ADDED_BY_FRIEND).whereIdea(idea).terminates();
            }
        }

        buildResponse(response, sr);
    }
}
