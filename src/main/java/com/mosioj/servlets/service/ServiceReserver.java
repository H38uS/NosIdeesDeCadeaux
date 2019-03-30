package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/reserver")
public class ServiceReserver extends AbstractService<IdeaInteractionBookingUpToDate> {

	private static final long serialVersionUID = 2642366164643542379L;
	private static final String IDEA_ID_PARAM = "idee";

	private static final Logger logger = LogManager.getLogger(ServiceReserver.class);

	public ServiceReserver() {
		super(new IdeaInteractionBookingUpToDate(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer idea = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		int userId = thisOne.id;
		logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea, userId));

		if (model.idees.canBook(idea, userId)) {
			model.idees.reserver(idea, userId);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", "ok"));
	}
}
