package com.mosioj.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Groupe;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/rechercher_groupe")
public class RechercherGroupe extends HttpServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	public static final String FORM_URL = "/protected/rechercher_groupe.jsp";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String groupeName = ParametersUtils.readIt(request, "name").trim();
		try {
			request.setAttribute("groupes", Groupe.getGroupe(groupeName));
			RootingsUtils.rootToPage(FORM_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericError(e, request, response);
		}
	}

}
