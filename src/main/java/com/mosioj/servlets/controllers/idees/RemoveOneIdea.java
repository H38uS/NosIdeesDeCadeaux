package com.mosioj.servlets.controllers.idees;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/remove_an_idea")
public class RemoveOneIdea extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final String PROTECTED_MA_LISTE = "/protected/ma_liste";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Reading parameters
		Integer id = ParametersUtils.readInt(request, "ideeId");
		if (id == null) {
			RootingsUtils.rootToPage(PROTECTED_MA_LISTE, request, response);
			return;
		}

		try {
			int userId = ParametersUtils.getUserId(request);
			idees.remove(userId, id);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
			return;
		}

		RootingsUtils.rootToPage(PROTECTED_MA_LISTE, request, response);
	}

}
