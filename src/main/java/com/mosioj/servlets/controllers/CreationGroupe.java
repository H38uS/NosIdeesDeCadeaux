package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;

@WebServlet("/protected/creation_groupe")
public class CreationGroupe extends IdeesCadeauxServlet {

	public static final String SUCCESS_URL = "/protected/creation_groupe_succes.jsp";
	public static final String EXISTS_URL = "/protected/existing_groupe.jsp";
	public static final String FORM_URL = "/protected/creation_groupe.jsp";
	private static final long serialVersionUID = -6329056607731725444L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String groupeName = ParametersUtils.readAndEscape(request, "name").trim();
		ParameterValidator validator = new ParameterValidator(groupeName, "nom du groupe", "Le ");
		validator.checkEmpty();
		validator.checkSize(-1, 50);

		List<String> errors = validator.getErrors();
		if (!errors.isEmpty()) {
			request.setAttribute("name_errors", errors);
			RootingsUtils.rootToPage(FORM_URL, request, response);
			return;
		}

		// Vérification du groupe
		int userId = ParametersUtils.getUserId(request);
		boolean hasAGroup = false;
		try {
			hasAGroup = groupes.hasAGroup(userId);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
			return;
		}

		if (hasAGroup) {
			RootingsUtils.rootToPage(EXISTS_URL, request, response);
			return;
		}

		// Création du groupe
		try {
			groupes.createGroup(groupeName, userId);
			RootingsUtils.rootToPage(SUCCESS_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(req);
		boolean hasAGroup = false;
		try {
			hasAGroup = groupes.hasAGroup(userId);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}

		if (hasAGroup) {
			RootingsUtils.rootToPage(EXISTS_URL, req, resp);
			return;
		}

		// On affiche le formulaire
		RootingsUtils.rootToPage(FORM_URL, req, resp);
	}

}
