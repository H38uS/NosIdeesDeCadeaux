package com.mosioj.ideescadeaux.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/annuler_sous_reservation")
public class AnnulerSousReservation extends AbstractIdea<IdeaInteraction> {

	private static final long serialVersionUID = 4998191671705040181L;
	private static final Logger logger = LogManager.getLogger(AnnulerSousReservation.class);
	private static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor.
	 */
	public AnnulerSousReservation() {
		super(new IdeaInteraction(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();

		
		RootingsUtils.redirectToPage(DetailSousReservation.URL + "?" + IDEA_ID_PARAM + "=" +idea.getId(), request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User user = thisOne;
		Idee idea = policy.getIdea();

		if (model.idees.isSubBookBy(idea.getId(), user)) {
			logger.debug(MessageFormat.format("Suppression des sous réservations de {0} sur l''idée {1}", user, idea.getId()));
			model.idees.dereserverSousPartie(idea.getId(), user);
		}

		RootingsUtils.redirectToPage(DetailSousReservation.URL + "?" + IDEA_ID_PARAM + "=" +idea.getId(), request, response);
	}

}
