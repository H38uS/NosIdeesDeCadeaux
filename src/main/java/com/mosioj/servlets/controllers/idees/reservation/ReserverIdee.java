package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/reserver")
public class ReserverIdee extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 7349100644264613480L;
	private static final String IDEA_ID_PARAM = "idee";

	/**
	 * Class constructor
	 */
	public ReserverIdee() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEA_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer idea = ParametersUtils.readInt(req, IDEA_ID_PARAM);
		int userId = ParametersUtils.getUserId(req);

		if (idees.canBook(idea, userId)) {
			idees.reserver(idea, userId);
		}

		RootingsUtils.rootToPage(MesListes.PROTECTED_MES_LISTES, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
