package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.CONFIRMED_UP_TO_DATE;

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
        ServiceResponse<?> sr = ServiceResponse.ok(idea.getId(), thisOne);

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
                sr = ServiceResponse.ko(message.toString(), thisOne);
            } else {
                logger.info("Modifying the idea {} of {}. Parameters: ['{}' / '{}' / '{}']",
                            idea.getId(),
                            idea.getOwner(),
                            parameters.get("text"),
                            parameters.get("type"),
                            parameters.get("priority"));
                String image = parameters.get("image");
                String old = idea.getImage();
                if (StringUtils.isBlank(image) || "null".equals(image)) {
                    // pas de nouvelle image
                    image = old;
                    logger.debug("No new image... Keeping {}.", old);
                } else {
                    // Modification de l'image
                    // On supprime la précédente
                    if (!"default.png".equals(old) && !StringUtils.isBlank(old)) {
                        IdeaLogic.removeUploadedImage(ParametersUtils.getIdeaPicturePath(), old);
                    }
                    logger.debug("Updating image from {} to {}.", old, image);
                }

                String text = Escaper.escapeIdeaText(StringEscapeUtils.unescapeHtml4(parameters.get("text")));
                idea.text = Escaper.transformSmileyToCode(text);
                idea.categorie = CategoriesRepository.getCategory(parameters.get("type")).orElse(null);
                idea.priority = PrioritiesRepository.getPriority(Integer.parseInt(parameters.get("priority")))
                                                    .orElse(null);
                idea.image = image;
                HibernateUtil.update(idea);

                // Ajout de notification aux amis si l'anniversaire approche
                IdeaLogic.addModificationNotification(thisOne, policy.getIdea(), false);

                // Suppression des demandes si y'en avait
                IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

                // Mise à jour des demandes de confirmations si à jour
                final Notification confirmationUpToDate = CONFIRMED_UP_TO_DATE.with(thisOne, idea);
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
