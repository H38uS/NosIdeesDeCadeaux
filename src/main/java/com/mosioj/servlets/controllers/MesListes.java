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

import com.mosioj.model.Group;
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

		String action = ParametersUtils.readAndEscape(req, "action");

		LOGGER.info(MessageFormat.format("Gets the lists for {0}", ParametersUtils.getUserName(req)));
		LOGGER.info(MessageFormat.format("Action: {0}", action));

		// FIXME : trier les listes, mettre sa liste en haut
		
		String displayThisGroup = ParametersUtils.readIt(req, "group");
		int groupIdToDisplay = -1;
		if (displayThisGroup != null) {
			try {
				groupIdToDisplay = Integer .parseInt(displayThisGroup);
				LOGGER.info(MessageFormat.format("Display only group: {0}", displayThisGroup));
			} catch (NumberFormatException e) {
			}
		}

		int userId = ParametersUtils.getUserId(req);
		if (!action.isEmpty()) {
			try {
				handleAction(action, req, userId);
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, req, resp);
				return;
			}
		}

		Set<User> ids = new HashSet<User>();
		ids.add(new User(userId));
		try {
			// Get all user id
			for (Group group : groupes.getGroupsJoined(userId, groupIdToDisplay)) {
				ids.addAll(groupes.getUsers(group.getId())); // TODO voir s'il ne faut pas mutualiser pour éviter les
																// constructions d'utilisateurs inutiles
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

	private void handleAction(String action, HttpServletRequest req, int userId)
			throws SQLException {

		String ideaParam = ParametersUtils.readAndEscape(req, "idee");
		Integer idea = ideaParam.isEmpty() ? null : Integer.parseInt(ideaParam);
		
		if (!hasRightOnIdea(idea, userId)) {
			return;
		}

		if ("reserver".equals(action) ) {
			if (idees.canBook(idea, userId)) {
				idees.reserver(idea, userId);
			}
		}

		if ("dereserver".equals(action)) {
			idees.dereserver(idea, userId);
		}
	}

	/**
	 * TODO peut être à bouger dans le modèle pour être utiliser par d'autres
	 * 
	 * @param idea
	 * @param userId
	 * @return True if we can interact with this idea.
	 * @throws SQLException 
	 */
	private boolean hasRightOnIdea(Integer idea, int userId) throws SQLException {
		
		if (idea == null) {
			return false;
		}
		
		if (!idees.isInScope(userId, idea)) {
			return false;
		}
		
		return true;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RootingsUtils.redirectToPage("/protected/mes_listes", request, response); // Rien de spécifique pour le moment
		// FIXME faire la réservation
		// FIXME gestion des groupes
		// TODO : pouvoir demander des informations et/ou discuter avec d'autres membres
	}

}
