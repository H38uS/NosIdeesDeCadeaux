package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends IdeesCadeauxGetAndPostServlet<IdeaModification> {

    private static final Logger logger = LogManager.getLogger(ModifyIdea.class);
    private static final long serialVersionUID = -1774633803227715931L;

    public static final String IDEE_ID_PARAM = "id";
    public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";
    public static final String PROTECTED_MODIFIER_IDEE = "/protected/modifier_idee";

    /**
     * Class constructor.
     */
    public ModifyIdea() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();

        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritesRepository.getPriorities());
        request.setAttribute("idea", idea);

        Object errors = request.getSession().getAttribute("errors");
        if (errors != null) {
            request.setAttribute("errors", errors);
            request.getSession().removeAttribute("errors");
        }

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    // FIXME : supprimer le post ? Déjà fait en service
    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException, IOException {

        Idee idea = policy.getIdea();

        // Check that we have a file upload request
        if (ServletFileUpload.isMultipartContent(request)) {

            final File ideaPicturePath = ParametersUtils.getIdeaPicturePath();
            final Map<String, String> parameters = ParametersUtils.readMultiFormParameters(request, ideaPicturePath);
            List<String> errors = IdeaLogic.fillIdeaOrErrors(parameters);

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
                String sb = PROTECTED_MODIFIER_IDEE +
                            "?" +
                            IDEE_ID_PARAM +
                            "=" +
                            idea.getId();
                RootingsUtils.redirectToPage(sb, request, response);
                return;
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

                List<AbstractNotification> notifications = NotificationsRepository.getNotification(ParameterName.IDEA_ID,
                                                                                                   idea.getId());
                for (AbstractNotification notification : notifications) {
                    if (notification instanceof NotifAskIfIsUpToDate) {
                        NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
                        NotificationsRepository.addNotification(isUpToDate.getUserIdParam(),
                                                                new NotifConfirmedUpToDate(user, idea));
                        NotificationsRepository.remove(notification);
                    }
                    if (notification instanceof NotifIdeaAddedByFriend) {
                        NotificationsRepository.remove(notification);
                    }
                }
            }

        }

        RootingsUtils.redirectToPage(AjouterIdee.PROTECTED_AJOUTER_IDEE + "?" + "id=" + idea.getId(),
                                     request,
                                     response);
    }

}
