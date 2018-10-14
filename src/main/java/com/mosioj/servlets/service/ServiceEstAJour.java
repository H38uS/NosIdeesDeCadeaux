package com.mosioj.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;

@WebServlet("/protected/service/est_a_jour")
public class ServiceEstAJour extends AbstractService {

	private static final long serialVersionUID = 2642366164643542379L;
	private static final String IDEE_FIELD_PARAMETER = "idee";

	public ServiceEstAJour() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		IdeaInteractions logic = new IdeaInteractions();
		Idee idea = getIdeeFromSecurityChecks();
		String status = logic.askIfUpToDate(idea, request) ? "ok" : "ko";

		writeJSonOutput(response, makeJSonPair("status", status));
	}
}
