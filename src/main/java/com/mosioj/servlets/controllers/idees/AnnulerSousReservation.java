package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;

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

@WebServlet("/protected/annuler_sous_reservation")
public class AnnulerSousReservation extends AbstractIdea {

	private static final long serialVersionUID = 4998191671705040181L;
	private static final Logger logger = LogManager.getLogger(AnnulerSousReservation.class);
	private static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor.
	 */
	public AnnulerSousReservation() {
		super(new IdeaInteraction(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = getIdeeFromSecurityChecks();
		RootingsUtils.redirectToPage(MessageFormat.format("{0}?{1}={2}", DetailSousReservation.URL, IDEA_ID_PARAM, idea.getId()), request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		Idee idea = getIdeeFromSecurityChecks();

		if (idees.isSubBookBy(idea.getId(), userId)) {
			logger.debug(MessageFormat.format("Suppression des sous réservations de {0} sur l''idée {1}", userId, idea.getId()));
			idees.dereserverSousPartie(idea.getId(), userId);
		}

		RootingsUtils.redirectToPage(MessageFormat.format("{0}?{1}={2}", DetailSousReservation.URL, IDEA_ID_PARAM, idea.getId()), request, response);
	}

}
