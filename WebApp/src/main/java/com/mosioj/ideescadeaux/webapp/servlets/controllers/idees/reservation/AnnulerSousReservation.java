package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
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

@WebServlet("/protected/annuler_sous_reservation")
public class AnnulerSousReservation extends IdeesCadeauxPostServlet<IdeaInteraction> {

    private static final long serialVersionUID = 4998191671705040181L;
    private static final Logger logger = LogManager.getLogger(AnnulerSousReservation.class);
    private static final String IDEA_ID_PARAM = "idee";

    /** Class constructor. */
    public AnnulerSousReservation() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        User user = thisOne;
        Idee idea = policy.getIdea();

        if (IdeesRepository.isSubBookBy(idea, user)) {
            logger.debug(MessageFormat.format("Suppression des sous réservations de {0} sur l''idée {1}",
                                              user,
                                              idea.getId()));
            IdeesRepository.dereserverSousPartie(idea, user);
        }

        final String url = DetailSousReservation.URL + "?" + IDEA_ID_PARAM + "=" + idea.getId();
        RootingsUtils.redirectToPage(url, request, response);
    }

}
