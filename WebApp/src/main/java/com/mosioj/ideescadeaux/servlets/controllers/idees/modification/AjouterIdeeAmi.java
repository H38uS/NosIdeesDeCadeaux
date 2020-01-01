package com.mosioj.ideescadeaux.servlets.controllers.idees.modification;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.model.repositories.PrioritesRepository;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

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
                boolean estSurprise = false;
                if ("on".equals(parameters.get("est_surprise"))) {
                    if (addedToUser.id != currentUser.id) {
                        estSurprise = true;
                    }
                }
                int ideaId = IdeesRepository.addIdea(addedToUser,
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     Integer.parseInt(parameters.get("priority")),
                                                     parameters.get("image"),
                                                     estSurprise ? currentUser : null,
                                                     currentUser);
                Idee idea = getIdeaAndEnrichIt(ideaId);
                request.setAttribute("idee", idea);

                if (!estSurprise) {
                    NotificationsRepository.addNotification(addedToUser.id, new NotifIdeaAddedByFriend(currentUser, idea));
                    NotificationsRepository.removeAllType(addedToUser, NotificationType.NO_IDEA);
                }
            }

        }

        request.setAttribute("user", addedToUser);
        request.setAttribute("types", CategoriesRepository.getCategories());
        request.setAttribute("priorites", PrioritesRepository.getPriorities());

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
