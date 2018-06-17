package com.mosioj.servlets.controllers.idees.modification;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.controllers.idees.MaListe;
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/remove_an_idea")
public class RemoveOneIdea extends AbstractIdea {

	public static final String IDEE_ID_PARAM = "ideeId";

	public RemoveOneIdea() {
		super(new IdeaModification(idees, IDEE_ID_PARAM));
	}

	private static final long serialVersionUID = -1774633803227715931L;

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		removeIt(request, response);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		removeIt(request, response);
	}

	protected void removeIt(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException {
		IdeaInteractions logic = new IdeaInteractions();
		Idee idea = getIdeeFromSecurityChecks();
		logic.removeIt(idea, getIdeaPicturePath(), request);
		RootingsUtils.redirectToPage(getFrom(request, MaListe.PROTECTED_MA_LISTE), request, response);
	}
}
