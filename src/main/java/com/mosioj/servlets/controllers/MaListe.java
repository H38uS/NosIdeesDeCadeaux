package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/ma_liste")
public class MaListe extends IdeesCadeauxServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/ma_liste.jsp";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Idee> ideas = null;
		try {
			ideas = idees.getOwnerIdeas(ParametersUtils.getUserId(req));
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
		req.setAttribute("idees", ideas);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// TODO échapper comme avant (<span, les <br, etc.)

		// Reading parameters
		String text = ParametersUtils.readIt(request, "text").trim();
		String type = ParametersUtils.readIt(request, "type").trim();
		String priority = ParametersUtils.readIt(request, "priority").trim();
		
		// TODO ça peut planter si on fille pas les bons paramètres, mais ça ne fait rien...

		try {
			idees.addIdea(ParametersUtils.getUserId(request), text, type, priority);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
		
		doGet(request, response);
	}

}
