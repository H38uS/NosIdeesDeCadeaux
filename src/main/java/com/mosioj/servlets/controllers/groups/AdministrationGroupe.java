package com.mosioj.servlets.controllers.groups;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/administration_groupe")
public class AdministrationGroupe extends DefaultGroupServlet {

	public static final String PROTECTED_ADMINISTRATION_GROUPE = "/protected/administration_groupe";
	public static final String FORM_URL = "/protected/administration_groupe.jsp";
	public static final String ERROR_URL = "/protected/administration_groupe_error.jsp";

	private static final Logger logger = LogManager.getLogger(AdministrationGroupe.class);
	private static final long serialVersionUID = -8940314241887215166L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(req);
		
		List<Integer> groups = groupes.getGroupsToAdmin(userId);
		
		int id = 0;
		try {

			id = groupes.getOwnerGroupId(userId);

		} catch (SQLException e) {
			if (groups.isEmpty()) {
				logger.error("Impossible de récupérer le groupe depuis le user " + userId + ". Erreur: " + e.getMessage());
				req.setAttribute(	"error_message",
						"Vous n'avez pas encore créé un groupe, vous ne pouvez donc pas l'administrer.");
				RootingsUtils.rootToPage(ERROR_URL, req, resp);
				return;
			} else {
				id = groups.get(0);
			}
		}

		try {
			req.setAttribute("demandes", groupesJoinRequest.getDemandes(id));
			req.setAttribute("groupId", id);
			req.setAttribute("isOwner", groupes.isGroupOwner(userId, id));
			req.setAttribute("groupName", groupes.getName(id));
			req.setAttribute("members", groupes.getUsers(id));
			req.setAttribute("admins", groupes.getAdmins(id));
			RootingsUtils.rootToPage(FORM_URL, req, resp);
		} catch (SQLException e) {
			logger.error("Erreur SQL: " + e.getMessage());
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
	}
	
	// TODO : pouvoir supprimer des membres (enfin pas soit)
	// TODO : pouvoir discuter d'une idée avec les membres du groupe, mais pas la personne

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(request);
		String pGroupId = request.getParameter("groupId");
		int groupId = -1;
		try {
			groupId = Integer.parseInt(pGroupId);
		} catch (NumberFormatException e) {
			logger.error("Groupe incorrect (" + pGroupId + "). Erreur: " + e.getMessage());
			request.setAttribute("error_message", "Le groupe fourni n'existe pas.");
			RootingsUtils.rootToPage(ERROR_URL, request, response);
			return;
		}

		try {
			if (!isAdminOf(request, response, groupId, userId)) {
				return;
			}

			Map<String, String[]> params = request.getParameterMap();
			for (String key : params.keySet()) {

				if (!key.startsWith("choix")) {
					continue;
				}

				String id = key.substring("choix_".length());
				boolean accept = "Accepter".equals(params.get(key)[0]);

				if (accept) {
					logger.info(MessageFormat.format(	"Approbation de la demande. Utilisateur {0}, groupe {1}.",
														id,
														groupId));
					groupes.addAssociation(groupId, Integer.parseInt(id));
				} else {
					logger.info(MessageFormat.format("Refus de la demande. Utilisateur {0}, groupe {1}.", id, groupId));
					groupesJoinRequest.cancelRequest(groupId, Integer.parseInt(id));
				}
			}

			// Redirection à la page d'administration
			RootingsUtils.redirectToPage(PROTECTED_ADMINISTRATION_GROUPE, request, response);

		} catch (SQLException e) {
			e.printStackTrace();
			RootingsUtils.rootToPage(ERROR_URL, request, response);
			return;
		}
	}

}
