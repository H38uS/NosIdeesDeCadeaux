package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Categorie;
import com.mosioj.ideescadeaux.core.model.entities.Priorite;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.repositories.CategoriesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
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
import java.util.List;

@WebServlet("/protected/ajouter_idee")
public class AjouterIdee extends AbstractIdea<AllAccessToPostAndGet> {

    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(AjouterIdee.class);
    private static final long serialVersionUID = -1774633803227715931L;

    public static final String VIEW_PAGE_URL = "/protected/completer_ma_liste.jsp";
    public static final String PROTECTED_AJOUTER_IDEE = "/protected/ajouter_idee";

    /**
     * Class constructor.
     */
    public AjouterIdee() {
        // No security : we will see only our content.
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse resp) throws ServletException, SQLException {

        List<Categorie> cat = CategoriesRepository.getCategories();
        List<Priorite> prio = PrioritesRepository.getPriorities();

        request.setAttribute("types", cat);
        request.setAttribute("priorites", prio);

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException, SQLException {

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
                User user = thisOne;
                int ideaId = IdeesRepository.addIdea(user,
                                                     parameters.get("text"),
                                                     parameters.get("type"),
                                                     Integer.parseInt(parameters.get("priority")),
                                                     parameters.get("image"),
                                                     null,
                                                     user);

                IdeesRepository.getIdeaWithoutEnrichment(ideaId).ifPresent(
                        i -> {
                            try {
                                addModificationNotification(user, i, true);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                logger.error(e);
                            }
                        }
                );

                NotificationsRepository.removeAllType(user, NotificationType.NO_IDEA);
                RootingsUtils.redirectToPage(VoirListe.PROTECTED_VOIR_LIST +
                                             "?" +
                                             VoirListe.USER_ID_PARAM +
                                             "=" +
                                             user.id,
                                             request,
                                             response);
                return;
            }
        }
        ideesKDoGET(request, response);
    }

}
