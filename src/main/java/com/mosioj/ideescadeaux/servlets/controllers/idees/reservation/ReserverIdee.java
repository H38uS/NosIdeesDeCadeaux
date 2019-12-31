package com.mosioj.ideescadeaux.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifRecurentIdeaUnbook;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.MesListes;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/reserver")
public class ReserverIdee extends AbstractIdea<IdeaInteraction> {

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
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        int userId = thisOne.id;
        logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), userId));

        if (IdeesRepository.canBook(idea.getId(), userId)) {
            IdeesRepository.reserver(idea.getId(), userId);
            for (AbstractNotification n : NotificationsRepository.getNotification(ParameterName.IDEA_ID, idea.getId())) {
                if (n instanceof NotifRecurentIdeaUnbook) {
                    NotificationsRepository.remove(n.id);
                }
            }
        }

        RootingsUtils.redirectToPage(getFrom(request, MesListes.PROTECTED_MES_LISTES), request, response);
    }

}
