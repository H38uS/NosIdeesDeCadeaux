package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/reserver")
public class ReserverIdee extends AbstractIdea<IdeaInteraction> {

    // FIXME : supprimer ? Et mettre le service de partout
    private static final Logger logger = LogManager.getLogger(ReserverIdee.class);
    private static final long serialVersionUID = 7349100644264613480L;
    public static final String IDEA_ID_PARAM = "idee";

    /**
     * Class constructor
     */
    public ReserverIdee() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        RootingsUtils.rootToGenericSQLError(thisOne, new Exception("Unsupported"), request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        int userId = thisOne.id;
        logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), userId));

        if (IdeesRepository.canBook(idea.getId(), userId)) {
            IdeesRepository.reserver(idea.getId(), userId);
            for (AbstractNotification n : NotificationsRepository.getNotification(ParameterName.IDEA_ID,
                                                                                  idea.getId())) {
                if (n instanceof NotifRecurentIdeaUnbook) {
                    NotificationsRepository.remove(n);
                }
            }
        }

        RootingsUtils.redirectToPage(getFrom(request), request, response);
    }

}
