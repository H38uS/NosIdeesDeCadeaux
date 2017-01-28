package com.mosioj.servlets.controllers.groups;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/remove_admin_groupe")
public class RemoveAdminUserToGroup extends DefaultGroupServlet {

	private static final Logger logger = LogManager.getLogger(RemoveAdminUserToGroup.class);
	private static final long serialVersionUID = -8940314241887215166L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(request);
		String pGroupId = request.getParameter("groupId");
		String pAdmin = request.getParameter("admin");

		int groupId = -1;
		try {
			groupId = Integer.parseInt(pGroupId);
		} catch (NumberFormatException e) {
			logger.error("Groupe incorrect (" + pGroupId + "). Erreur: " + e.getMessage());
			request.setAttribute(	"error_message",
			                     	"Le groupe fourni n'existe pas.");
			RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
			return;
		}

		try {
			// Est-ce qu'on peut administrer le groupe ?
			if (!isGroupOwner(request, response, groupId, userId)) {
				return;
			}

			int userToRemove = -1;
			try {
				userToRemove = users.getId(pAdmin);
			} catch (SQLException e) {
				logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} de supprimer {1}...",
													userId,
													pAdmin));
				request.setAttribute(	"error_message",
				                     	"L'utilisateur n'existe pas...");
				RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
				return;
			}
			
			groupes.removeAdmin(groupId, userToRemove);

			// Redirection à la page d'administration
			redirectToAdminPage(request, response);
			return;

		} catch (SQLException e) {
			e.printStackTrace();
			RootingsUtils.rootToGenericSQLError(e, request, response);
			return;
		}
	}
}
