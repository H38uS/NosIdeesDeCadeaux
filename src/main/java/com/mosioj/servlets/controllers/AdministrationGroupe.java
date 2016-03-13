package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/administration_groupe")
public class AdministrationGroupe extends IdeesCadeauxServlet {

	public static final String FORM_URL = "/protected/administration_groupe.jsp";
	public static final String ERROR_URL = "/protected/administration_groupe_error.jsp";

	private static final long serialVersionUID = -8940314241887215166L;

	public AdministrationGroupe() {
		super();
	}

	public AdministrationGroupe(Groupes groupesManager, GroupeJoinRequests groupesJoinRequestManager) {
		super(groupesManager, groupesJoinRequestManager);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(req);
		int id = 0;
		try {

			id = groupes.getGroupId(userId);

		} catch (SQLException e) {
			req.setAttribute("error_message", "Vous n'avez pas encore créé un groupe, vous ne pouvez donc pas l'administrer.");
			RootingsUtils.rootToPage(ERROR_URL, req, resp);
			return;
		}

		try {
			req.setAttribute("demandes", groupesJoinRequest.getDemandes(id));
			req.setAttribute("groupId", id);
			req.setAttribute("members", groupes.getUsers(id));
			RootingsUtils.rootToPage(FORM_URL, req, resp);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(request);
		String pGroupId = request.getParameter("groupId");
		int groupId = -1;
		try {
			groupId = Integer.parseInt(pGroupId);
		} catch (NumberFormatException e) {
			request.setAttribute("error_message", "Le groupe fourni n'existe pas.");
			RootingsUtils.rootToPage(ERROR_URL, request, response);
			return;
		}

		try {
			if (!groupes.isGroupOwner(userId, groupId)) {
				request.setAttribute("error_message", "Vous ne pouvez administrer que votre groupe.");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}
			
			Map<String, String[]> params = request.getParameterMap();
			for (String key : params.keySet()) {
				
				if (!key.startsWith("choix")) {
					continue;
				}
				
				String id = key.substring("choix_".length());
				boolean accept = "Accepter".equals(params.get(key)[0]);
				
				if (accept) {
					groupes.addAssociation(groupId, Integer.parseInt(id));
				} else {
					groupesJoinRequest.cancelRequest(groupId, Integer.parseInt(id));
				}
			}

			// Redirection à la page d'administration
			doGet(request, response);
			
		} catch (SQLException e) {
			RootingsUtils.rootToPage(ERROR_URL, request, response);
			return;
		}
	}

}
