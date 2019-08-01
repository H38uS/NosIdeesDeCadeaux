package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/dereserver")
public class DereserverIdee extends AbstractIdea<IdeaInteraction> {

	private static final Logger logger = LogManager.getLogger(DereserverIdee.class);
	private static final long serialVersionUID = -8244829899125982644L;
	private static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor
	 */
	public DereserverIdee() {
		super(new IdeaInteraction(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		Idee idea = policy.getIdea();
		int userId = thisOne.id;

		logger.debug(MessageFormat.format("Annulation de la réservation de l''idée {0} par {1}.", idea.getId(), userId));
		model.idees.dereserver(idea.getId(), userId);

		RootingsUtils.redirectToPage(getFrom(request, MesListes.PROTECTED_MES_LISTES), request, resp); // TODO doit être fait en post
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
