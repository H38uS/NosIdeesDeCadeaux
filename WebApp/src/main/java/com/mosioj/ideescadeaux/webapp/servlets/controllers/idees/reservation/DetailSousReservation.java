package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
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
import java.util.List;

@WebServlet("/protected/detail_sous_reservation")
public class DetailSousReservation extends AbstractIdea<IdeaInteraction> {

    private static final long serialVersionUID = -2188278918134412556L;
    private static final Logger logger = LogManager.getLogger(DetailSousReservation.class);

    private static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/detail_sous_reservation.jsp";
    public static final String URL = "/protected/detail_sous_reservation";

    /**
     * Class constructor.
     */
    public DetailSousReservation() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    // FIXME : Pouvoir suggérer de sous réserver cette idée

    private void setupCommon(HttpServletRequest req, Idee idea, User user) {

        logger.debug("Getting partial booking details for idea " + idea.getId() + "...");
        req.setAttribute("idee", idea);

        List<SousReservationEntity> reservations = SousReservationRepository.getSousReservation(idea.getId());
        req.setAttribute("sous_reservation_existantes", reservations);
        boolean isInThere = false;
        for (SousReservationEntity entity : reservations) {
            if (entity.user == user) {
                isInThere = true;
                break;
            }
        }
        req.setAttribute("fait_parti_sous_reservation", isInThere);
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        setupCommon(request, idea, thisOne);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        setupCommon(request, idea, thisOne);
        if (sousReserver(request, response, thisOne, idea, VIEW_PAGE_URL)) {
            RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
        }
    }

}
