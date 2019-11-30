package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;

@WebServlet("/protected/service/reserver")
public class ServiceReserver extends AbstractServicePost<IdeaInteraction> {

	private static final long serialVersionUID = 2642366164643542379L;
	private static final String IDEA_ID_PARAM = "idee";

	private static final Logger logger = LogManager.getLogger(ServiceReserver.class);

	public ServiceReserver() {
		super(new IdeaInteraction(IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();
		logger.debug(MessageFormat.format("Réservation de l''idée {0} par {1}.", idea.getId(), thisOne));

		if (model.idees.canBook(idea.getId(), thisOne.id)) {
			model.idees.reserver(idea.getId(), thisOne.id);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", "ok"));
	}
}
