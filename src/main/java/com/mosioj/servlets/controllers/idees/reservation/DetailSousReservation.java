package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.SousReservationEntity;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/detail_sous_reservation")
public class DetailSousReservation extends AbstractIdea {

	private static final long serialVersionUID = -2188278918134412556L;
	private static final Logger logger = LogManager.getLogger(DetailSousReservation.class);

	private static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/detail_sous_reservation.jsp";
	public static final String URL = "/protected/detail_sous_reservation";

	/**
	 * Class constructor.
	 */
	public DetailSousReservation() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEA_ID_PARAM));
	}

	// TODO : Pouvoir suggérer de sous réserver cette idée

	private void setupCommon(HttpServletRequest req, Idee idea, int userId) throws SQLException {

		logger.debug("Getting partial booking details for idea " + idea.getId() + "...");
		req.setAttribute("idee", idea);

		List<SousReservationEntity> reservations = sousReservation.getSousReservation(idea.getId());
		req.setAttribute("sous_reservation_existantes", reservations);
		boolean isInThere = false;
		for (SousReservationEntity entity : reservations) {
			if (entity.user.id == userId) {
				isInThere = true;
				break;
			}
		}
		req.setAttribute("fait_parti_sous_reservation", isInThere);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Idee idea = getIdeeFromSecurityChecks();
		int userId = ParametersUtils.getUserId(req);

		setupCommon(req, idea, userId);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);

	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		Idee idea = getIdeeFromSecurityChecks();

		setupCommon(request, idea, userId);

		if (sousReserver(request, response, userId, idea, VIEW_PAGE_URL)) {
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
		}
	}

}
