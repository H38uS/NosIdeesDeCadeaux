package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/add_admin_groupe")
public class AddAdminUserToGroup extends IdeesCadeauxServlet {

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
			request.setAttribute(	"error_message",
			                     	"Le groupe fourni n'existe pas.");
			RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
			return;
		}

		try {
			// Est-ce qu'on peut administrer le groupe ?
			// FIXME faire une gestion de droit centraliser
			if (!groupes.isGroupOwner(userId, groupId)) {
				logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} d''administrer le groupe {1}.",
													userId,
													groupId));
				request.setAttribute(	"error_message",
				                     	"Vous ne pouvez administrer que vos groupes.");
				RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
				return;
			}

			int adminId = -1;
			try {
				adminId = users.getId(pAdmin); // FIXME : ne marche qu'avec l'email...
			} catch (SQLException e) {
				logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} d''ajouter {1}...",
													userId,
													pAdmin));
				request.setAttribute(	"error_message",
				                     	"L'utilisateur n'existe pas...");
				RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
				return;
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
				request.setAttribute(	"error_message",
				                     	pAdmin + " est déjà administrateur du groupe !");
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

	private void redirectToAdminPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext context = getServletContext();
		RequestDispatcher rd = context.getRequestDispatcher("/protected/administration_groupe");
		rd.forward(request, response);
	}

}
