package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.NetworkInteractions;
import com.mosioj.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/demande_rejoindre_reseau")
public class DemandeRejoindreReseau extends IdeesCadeauxServlet<PeutDemanderARejoindreLeReseau> {

	private static final long serialVersionUID = -7941136326499438776L;

	public static final String SUCCESS_URL = "/protected/demande_rejoindre_reseau_succes.jsp";
	public static final String ERROR_URL = "/protected/demande_rejoindre_reseau_error.jsp";

	/**
	 * Class constructor.
	 */
	public DemandeRejoindreReseau() {
		super(new PeutDemanderARejoindreLeReseau(userRelations, userRelationRequests, NetworkInteractions.USER_ID_PARAM));
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		new NetworkInteractions().sendARequest(request);
		RootingsUtils.rootToPage(SUCCESS_URL, request, response);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		req.setAttribute("error_message", "Aucun utilisateur spécifié !");
		RootingsUtils.rootToPage(ERROR_URL, req, resp);
	}

}
