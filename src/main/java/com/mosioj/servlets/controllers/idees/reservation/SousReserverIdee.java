package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/sous_reserver")
public class SousReserverIdee extends AbstractIdea {

	private static final long serialVersionUID = 7349100644264613480L;
	private static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/sous_reservation.jsp";

	/**
	 * Class constructor
	 */
	public SousReserverIdee() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Idee idea = getIdeeFromSecurityChecks();
		req.setAttribute("idea", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		Idee idea = getIdeeFromSecurityChecks();
		request.setAttribute("idea", idea);

		if (sousReserver(request, response, userId, idea, VIEW_PAGE_URL)) {
			RootingsUtils.redirectToPage(MessageFormat.format("{0}?{1}={2}", DetailSousReservation.URL, IDEA_ID_PARAM, idea.getId()), request, response);
		}

	}
}
