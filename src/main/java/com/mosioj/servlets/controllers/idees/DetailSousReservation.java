package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/detail_sous_reservation")
public class DetailSousReservation extends AbstractIdea {

	private static final long serialVersionUID = -2188278918134412556L;
	private static final Logger logger = LogManager.getLogger(DetailSousReservation.class);

	private static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/detail_sous_reservation.jsp";

	/**
	 * Class constructor.
	 */
	public DetailSousReservation() {
		super(new IdeaInteraction(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Idee idea = getIdeeFromSecurityChecks();
		req.setAttribute("idea", idea);

		logger.debug("Getting partial booking details for idea " + idea.getId() + "...");

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);

	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// FIXME : 0 fignoler le détail
		// FIXME : 0 voir quand on annule les réservations
		// FIXME : 0 pouvoir supprimer sa propre sous partie
		// FIXME : 0 faire la notification pour proposer à quelqu'un de sous réserver
		int userId = ParametersUtils.getUserId(request);
		Idee idea = getIdeeFromSecurityChecks();
		request.setAttribute("idea", idea);

		if (sousReserver(request, response, userId, idea, VIEW_PAGE_URL)) {
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
			// FIXME faire quelque chose
			// RootingsUtils.rootToPage(SuggestGroupIdea.VIEW_URL + "?" + SuggestGroupIdea.GROUP_ID_PARAM + "=" + 10,
			// request, response);
		}
	}

}
