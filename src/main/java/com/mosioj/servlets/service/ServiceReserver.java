package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;

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

		Idee idea = policy.getIdea();
		int userId = thisOne.id;
		logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), userId));

		if (model.idees.canBook(idea.getId(), userId)) {
			model.idees.reserver(idea.getId(), userId);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", "ok"));
	}
}
