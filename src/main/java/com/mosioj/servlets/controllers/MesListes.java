package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Groupe;
import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mes_listes")
public class MesListes extends IdeesCadeauxServlet {

	/**
	 * Class logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(MesListes.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = ParametersUtils.readIt(req, "action");

		LOGGER.info(MessageFormat.format("Gets the lists for {0}", ParametersUtils.getUserName(req)));
		LOGGER.info(MessageFormat.format("Action: {0}", action));

		// FIXME : trier les listes, mettre sa liste en haut

		if ("reserver".equals(action)) {
			int idea = Integer.parseInt(ParametersUtils.readIt(req, "idee"));
			try {
				idees.reserver(idea, ParametersUtils.getUserId(req));
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, req, resp);
				return;
			}
		}
		Set<User> ids = new HashSet<User>();
		try {
			// Get all user id
			LOGGER.debug("Getting all groups that the user belongs to...");
			for (Groupe group : groupes.getGroupsJoined(ParametersUtils.getUserId(req))) {
				ids.addAll(groupes.getUsers(group.getId()));
			}
			
			LOGGER.debug("Getting all ideas for all users...");
			for (User user : ids) {
				user.addIdeas(idees.getOwnerIdeas(user.id));
			}
			
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}
		
		req.setAttribute("users", ids);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response); // Rien de spécifique pour le moment
		// FIXME faire la réservation
		// FIXME gestion des groupes
		// FIXME : pouvoir demander des informations et/ou discuter avec d'autres membres
	}

}
