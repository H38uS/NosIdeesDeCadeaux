package com.mosioj.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.mosioj.servlets.controllers.idees.reservation.DereserverIdee;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/dereserver")
public class ServiceDereserver extends AbstractService {

	private static final Logger logger = LogManager.getLogger(DereserverIdee.class);
	private static final long serialVersionUID = -8244829899125982644L;
	private static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor
	 */
	public ServiceDereserver() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer idea = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		int userId = ParametersUtils.getUserId(request);

		logger.debug(MessageFormat.format("Annulation de la réservation de l''idée {0} par {1}.", idea, userId));
		idees.dereserver(idea, userId);

		try {
			writeJSonOutput(response, JSONObject.toString("status", "ok"));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}