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

@WebServlet("/protected/rechercher_groupe")
public class RechercherGroupe extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	public static final String FORM_URL = "/protected/rechercher_groupe.jsp";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String groupeName = ParametersUtils.readIt(request, "name").trim();
		try {
			request.setAttribute("groupes", groupes.getGroupsToJoin(groupeName, ParametersUtils.getUserId(request)));
			RootingsUtils.rootToPage(FORM_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	// TODO : ne pas afficher le bouton rejoindre groupe si on a déjà envoyé une demande...

}
