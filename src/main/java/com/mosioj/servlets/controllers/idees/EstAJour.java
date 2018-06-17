package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.logichelpers.IdeaInteractions;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/est_a_jour")
public class EstAJour extends AbstractIdea {

	private static final long serialVersionUID = -2229577569569388562L;
	private static final String IDEE_FIELD_PARAMETER = "idee";
	private static final String VIEW_PAGE_URL = "est_a_jour_succes.jsp";
	private static final String ERROR_PAGE_URL = "est_a_jour_error.jsp";

	public EstAJour() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		Idee idea = getIdeeFromSecurityChecks();
		IdeaInteractions logic = new IdeaInteractions();

		if (logic.askIfUpToDate(idea, request)) {
			request.setAttribute("text", idea.getTextSummary(50));
			request.setAttribute("user", idea.owner.name);
			request.setAttribute("link", getFrom(request, MesListes.PROTECTED_MES_LISTES).substring(1));
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
		} else {
			request.setAttribute("name", idea.owner.name);
			request.setAttribute("error_message", "Une demande sur cette idée a déjà été envoyée.");
			request.setAttribute("link", getFrom(request, MesListes.PROTECTED_MES_LISTES).substring(1));
			RootingsUtils.rootToPage(ERROR_PAGE_URL, request, resp);
		}

	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
