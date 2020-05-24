package com.mosioj.ideescadeaux.webapp.servlets.service;


import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/protected/service/ajouter_idee")
public class ServiceAjouterIdee extends ServicePost<NetworkAccess> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(AjouterIdee.class);

    public static final String USER_PARAMETER = "id";

    public ServiceAjouterIdee() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        ServiceResponse<?> sr;

        final User addedToUser = policy.getUser();
        final boolean isItByMeForMe = thisOne.equals(addedToUser);

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

                // Ajout de l'idée
                boolean estSurprise = "on".equals(parameters.get("est_surprise")) && !isItByMeForMe;
                int ideaId = IdeesRepository.addIdea(addedToUser,
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     Integer.parseInt(parameters.get("priority")),
                                                     parameters.get("image"),
                                                     estSurprise ? thisOne : null,
                                                     thisOne);
                logger.info("Idea {} [{} / {} / {}] added.",
                            ideaId,
                            parameters.get("text"),
                            parameters.get("type"),
                            parameters.get("priority"));

                // Gestion des notifications
                final Optional<Idee> idea = IdeesRepository.getIdea(ideaId);
                idea.ifPresent(i -> IdeaLogic.addModificationNotification(addedToUser, i, true));
                if (isItByMeForMe) {
                    // Pour soit : uniquement la notif plus d'idée
                    NotificationsRepository.removeAllType(thisOne, NotificationType.NO_IDEA);
                } else {
                    // Si ce n'est pas une surprise, envoie de notification
                    // Et suppression des notifications NO_IDEA
                    idea.filter(i -> !estSurprise).ifPresent(i -> {
                        NotificationsRepository.addNotification(addedToUser.id,
                                                                new NotifIdeaAddedByFriend(thisOne, i));
                        NotificationsRepository.removeAllType(addedToUser, NotificationType.NO_IDEA);
                    });
                }

                sr = ServiceResponse.ok(ideaId, isAdmin(request), thisOne);
            }
        } else {
            sr = ServiceResponse.ko("Le formulaire est incorrect...", isAdmin(request), thisOne);
        }

        buildResponse(response, sr);
    }
}
