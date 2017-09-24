package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	public static final String FORM_URL = "/protected/rechercher_personne.jsp";

	/**
	 * Class constructor.
	 */
	public RechercherPersonne() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		boolean onlyNonFriend = "on".equals(ParametersUtils.readAndEscape(request, "only_non_friend").trim());

		List<User> foundUsers = users.getUsers(userNameOrEmail, 20);
		int userId = ParametersUtils.getUserId(request);
		foundUsers.remove(users.getUser(userId));
		List<User> friends = userRelations.getAllUsersInRelation(userId);
		if (onlyNonFriend) {
			foundUsers.removeAll(friends);
		} else {
			for (User user : foundUsers) {
				user.isInMyNetwork = friends.contains(user);
			}
		}
		
		for (User user : foundUsers) {
			if (userRelationRequests.associationExists(userId, user.id)) {
				user.freeComment = "Vous avez déjà envoyé <br/>une demande à " + user.getName();
			}
		}

		request.setAttribute("users", foundUsers);
		request.setAttribute("name", userNameOrEmail);
		request.setAttribute("onlyNonFriend", onlyNonFriend);

		RootingsUtils.rootToPage(FORM_URL, request, response);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		RootingsUtils.rootToPage(FORM_URL, req, resp);
	}

	// FIXME : pouvoir afficher les pages suivantes

}
