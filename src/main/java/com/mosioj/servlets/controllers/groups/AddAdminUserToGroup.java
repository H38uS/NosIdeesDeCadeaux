package com.mosioj.servlets.controllers.groups;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.database.NoRowsException;

@WebServlet("/protected/add_admin_groupe")
public class AddAdminUserToGroup extends DefaultGroupServlet {

	private static final Logger logger = LogManager.getLogger(AddAdminUserToGroup.class);
	private static final long serialVersionUID = -8940314241887215166L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(request);
		String pGroupId = request.getParameter("groupId");
		String pAdmin = request.getParameter("admin");

		if (pAdmin.isEmpty()) {
			redirectToAdminPage(request, response);
			return;
		}

		int groupId = -1;
		try {
			groupId = Integer.parseInt(pGroupId);
		} catch (NumberFormatException e) {
			logger.error("Groupe incorrect (" + pGroupId + "). Erreur: " + e.getMessage());
			request.setAttribute("error_message", "Le groupe fourni n'existe pas.");
			RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
			return;
		}

		try {
			// Est-ce qu'on peut administrer le groupe ?
			if (!isGroupOwner(request, response, groupId, userId)) {
				return;
			}

			int adminId = -1;
			try {
				adminId = users.getId(pAdmin);
			} catch (NoRowsException e) {

				// Est-ce un nom ?
				List<User> potUsers = users.getUsersToAdmin(pAdmin, groupId);
				if (potUsers.size() == 0) {
					logger.error(MessageFormat.format("Essai de l''utilisateur {0} d''ajouter {1}...", userId, pAdmin));
					request.setAttribute("error_message", "L'utilisateur n'existe pas ou fait déjà parti des administrateurs...");
					RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
					return;
				}

				if (potUsers.size() == 1) {
					// Got it !
					adminId = potUsers.get(0).id;
				} else {
					// Plusieurs correspondance... On demande l'email.
					logger.debug("Demande d'ajout de " + pAdmin + "... Mais plusieurs entrées détectées.");
					request.setAttribute("potential_emails", potUsers);
					request.setAttribute("potential_name", pAdmin);
					// Redirection à la page d'administration
					redirectToAdminPage(request, response);
					return;
				}
			}

			if (!groupes.associationExists(groupId, adminId)) {
				logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} d''ajouter {1} (non dans le groupe)...",
													userId,
													pAdmin));
				request.setAttribute(	"error_message",
										"Vous ne pouvez ajouter que des membres du groupe aux administrateurs.");
				RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
				return;
			}

			if (groupes.isAdminOf(groupId, adminId)) {
				logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} d''ajouter {1} qui y est déjà...",
													userId,
													pAdmin));
				request.setAttribute("error_message", pAdmin + " est déjà administrateur du groupe !");
				RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
				return;
			}

			groupes.addAdmin(groupId, adminId);

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
