package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/demande_rejoindre_groupe")
public class DemandeRejoindreGroupe extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -7941136326499438776L;

	public static final String SUCCESS_URL = "/protected/demande_rejoindre_groupe_succes.jsp";
	public static final String ERROR_URL = "/protected/demande_rejoindre_groupe_error.jsp";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			String group = ParametersUtils.readIt(request, "groupe_id");
			if (group.isEmpty()) {
				throw new SQLException("Aucun groupe spécifié !");
			}

			int groupId = Integer.parseInt(group);

			int userId = ParametersUtils.getUserId(request);
			request.setAttribute("name", groupes.getName(groupId));

			if (groupes.associationExists(groupId, userId)) {
				request.setAttribute("error_message", "Vous faites déjà parti de ce groupe.");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			if (groupesJoinRequest.associationExists(userId, groupId)) {
				request.setAttribute("error_message", "Vous avez déjà envoyé une demande pour ce groupe.");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			// On ajoute l'association
			groupesJoinRequest.insert(userId, groupId);
			RootingsUtils.rootToPage(SUCCESS_URL, request, response);

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

}
