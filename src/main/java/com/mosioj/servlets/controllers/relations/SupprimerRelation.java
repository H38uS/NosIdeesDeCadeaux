package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.NetworkInteractions;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_relation")
public class SupprimerRelation extends IdeesCadeauxServlet<NetworkAccess> {

	private static final long serialVersionUID = 2491763819457048609L;
	public static final String USER_PARAMETER = "id";

	public SupprimerRelation() {
		super(new NetworkAccess(userRelations, USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(AfficherReseau.SELF_VIEW + "?id=" + ParametersUtils.getUserId(request), request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Integer user = ParametersUtils.readInt(request, USER_PARAMETER);
		int currentId = ParametersUtils.getUserId(request);
		new NetworkInteractions().deleteRelationship(currentId, user);
		RootingsUtils.redirectToPage(AfficherReseau.SELF_VIEW + "?id=" + currentId, request, response);
	}

}
