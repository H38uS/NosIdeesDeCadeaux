package com.mosioj.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.IdeaModification;

@WebServlet("/protected/service/delete_idea")
public class ServiceDeleteIdea extends AbstractService {

	private static final long serialVersionUID = 2642366164643542379L;
	public static final String IDEE_ID_PARAM = "ideeId";

	public ServiceDeleteIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		IdeaInteractions logic = new IdeaInteractions();
		Idee idea = getIdeeFromSecurityChecks();
		logic.removeIt(idea, getIdeaPicturePath(), request);

		writeJSonOutput(response, makeJSonPair("status", "ok"));
	}
}
