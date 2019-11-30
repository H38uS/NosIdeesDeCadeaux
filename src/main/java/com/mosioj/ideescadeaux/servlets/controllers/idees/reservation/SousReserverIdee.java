package com.mosioj.ideescadeaux.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/sous_reserver")
public class SousReserverIdee extends AbstractIdea<IdeaInteraction> {

	private static final long serialVersionUID = 7349100644264613480L;
	private static final Logger logger = LogManager.getLogger(SousReserverIdee.class);

	private static final String IDEA_ID_PARAM = "idee";
	public static final String URL = "/protected/sous_reserver";
	public static final String VIEW_PAGE_URL = "/protected/sous_reservation.jsp";

	/**
	 * Class constructor
	 */
	public SousReserverIdee() {
		super(new IdeaInteraction(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		req.setAttribute("idee", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();
		request.setAttribute("idee", idea);

		if (sousReserver(request, response, thisOne, idea, VIEW_PAGE_URL)) {
			String url = DetailSousReservation.URL + "?" + IDEA_ID_PARAM + "=" + idea.getId();
			logger.info(MessageFormat.format("Succès ! Redirection vers {0}...", url));
			RootingsUtils.redirectToPage(url, request, response);
		} else {
			String url = URL + "?" + IDEA_ID_PARAM + "=" + idea.getId();
			logger.info(MessageFormat.format("Echec ! Redirection vers {0}...", url));
			RootingsUtils.redirectToPage(url, request, response);
		}

	}
}
