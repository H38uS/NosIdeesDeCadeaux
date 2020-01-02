package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
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
import java.util.Optional;

@WebServlet("/protected/ajouter_idee_ami")
public class AjouterIdeeAmi extends AbstractIdea<NetworkAccess> {

    private static final long serialVersionUID = -7053283110787519597L;
    private static final Logger logger = LogManager.getLogger(AjouterIdeeAmi.class);

    public static final String USER_PARAMETER = "id";

    public static final String VIEW_PAGE_URL = "/protected/ajouter_idee_ami.jsp";

    public AjouterIdeeAmi() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        User user = policy.getUser();
        request.setAttribute("user", user);
        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritesRepository.getPriorities());

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException, IOException {

        User addedToUser = policy.getUser();

        // Check that we have a file upload request
        if (ServletFileUpload.isMultipartContent(request)) {

            fillIdeaOrErrors(request, response);

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
            } else {
                logger.info(MessageFormat.format("Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
                                                 parameters.get("text"),
                                                 parameters.get("type"),
                                                 parameters.get("priority")));
                User currentUser = thisOne;
                boolean estSurprise = "on".equals(parameters.get("est_surprise")) && addedToUser.id != currentUser.id;
                int ideaId = IdeesRepository.addIdea(addedToUser,
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     Integer.parseInt(parameters.get("priority")),
                                                     parameters.get("image"),
                                                     estSurprise ? currentUser : null,
                                                     currentUser);

                final Optional<Idee> idea = getIdeaAndEnrichIt(ideaId);
                idea.ifPresent(i -> request.setAttribute("idee", i));

                // Si ce n'est pas une surprise, envoie de notification
                // Et suppression des notifications NO_IDEA
                idea.filter(i -> !estSurprise).ifPresent(i -> {
                    NotificationsRepository.addNotification(addedToUser.id,
                                                            new NotifIdeaAddedByFriend(currentUser, i));
                    NotificationsRepository.removeAllType(addedToUser, NotificationType.NO_IDEA);
                });
            }

        }

        request.setAttribute("user", addedToUser);
        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritesRepository.getPriorities());

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
