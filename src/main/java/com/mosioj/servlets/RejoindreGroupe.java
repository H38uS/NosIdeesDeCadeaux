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

@WebServlet("/protected/rejoindre_groupe")
public class RejoindreGroupe extends HttpServlet {

	private static final long serialVersionUID = -7941136326499438776L;

	public static final String SUCCES_URL = "/protected/rejoindre_groupe_succes.jsp";
	public static final String ERROR_URL = "/protected/rejoindre_groupe_error.jsp";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int groupId = Integer.parseInt(ParametersUtils.readIt(request, "groupe_id"));
		
		try {
			// FIXME : il faut juste envoyer une demande et ensuite que le owner accepte
			int userId = ParametersUtils.getUserId(request);
			request.setAttribute("name", Groupe.getName(groupId));
			
			if (Groupe.associationExists(groupId, userId)) {
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			Groupe.addAssociation(groupId, userId);
			RootingsUtils.rootToPage(SUCCES_URL, request, response);

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

}
