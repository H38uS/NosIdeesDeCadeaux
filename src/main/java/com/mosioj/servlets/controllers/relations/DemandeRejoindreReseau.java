package com.mosioj.servlets.controllers.relations;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/demande_rejoindre_reseau")
public class DemandeRejoindreReseau extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -7941136326499438776L;

	public static final String SUCCESS_URL = "/protected/demande_rejoindre_reseau_succes.jsp";
	public static final String ERROR_URL = "/protected/demande_rejoindre_reseau_error.jsp";

	/**
	 * Class constructor.
	 */
	public DemandeRejoindreReseau() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			String user = ParametersUtils.readAndEscape(request, "user_id");
			if (user.isEmpty()) {
				request.setAttribute("error_message", "Aucun utilisateur spécifié !");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			User userToSendInvitation = users.getUser(Integer.parseInt(user));
			int userId = ParametersUtils.getUserId(request);
			
			request.setAttribute("name", userToSendInvitation.name);
			
			if (userToSendInvitation.id == userId || userRelations.associationExists(userToSendInvitation.id, userId)) {
				request.setAttribute("error_message", "Vous faites déjà parti du même réseau.");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			if (userRelationRequests.associationExists(userId, userToSendInvitation.id)) {
				request.setAttribute("error_message", "Vous avez déjà envoyé une demande pour cette personne.");
				RootingsUtils.rootToPage(ERROR_URL, request, response);
				return;
			}

			// On ajoute l'association
			userRelationRequests.insert(userId, userToSendInvitation.id);
			RootingsUtils.rootToPage(SUCCESS_URL, request, response);

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Nothing to do
	}

}
