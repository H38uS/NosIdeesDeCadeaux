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

@WebServlet("/protected/administration_groupe")
public class AdministrationGroupe extends HttpServlet {

	public static final String FORM_URL = "/protected/administration_groupe.jsp";
	public static final String ERROR_URL = "/protected/administration_groupe_error.jsp";

	private static final long serialVersionUID = -8940314241887215166L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(req);
		int id = 0;
		try {

			id = Groupe.getGroupId(userId);

		} catch (SQLException e) {
			RootingsUtils.rootToPage(ERROR_URL, req, resp);
			return;
		}

		try {
			req.setAttribute("members", Groupe.getUsers(id));
			RootingsUtils.rootToPage(FORM_URL, req, resp);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
	}

}
