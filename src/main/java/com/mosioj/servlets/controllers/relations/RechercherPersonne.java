package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.ArrayList;
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
	public static final String DEFAULT_FORM_URL = "/protected/rechercher_personne.jsp";
	public final String formUrl;

	/**
	 * Class constructor.
	 */
	public RechercherPersonne() {
		super(new AllAccessToPostAndGet());
		formUrl = DEFAULT_FORM_URL;
	}

	/**
	 * Class constructor.
	 */
	public RechercherPersonne(String dispatchURL) {
		super(new AllAccessToPostAndGet());
		formUrl = dispatchURL;
	}

	private void doTheLogic(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException {

		String pageNumberArg = ParametersUtils.readAndEscape(request, "page").trim();
		int pageNumber = 1;
		try {
			pageNumber = Integer.parseInt(pageNumberArg);
		} catch (NumberFormatException e) {
			// Nothing to do
		}

		final int MAX_NUMBER_OF_RESULT = 20;
		int firstRow = (pageNumber - 1) * MAX_NUMBER_OF_RESULT;
		int userId = ParametersUtils.getUserId(request);

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);

		List<User> foundUsers = users.getUsers(userNameOrEmail, userId, onlyNonFriend, firstRow, MAX_NUMBER_OF_RESULT);

		int total = foundUsers.size();
		if (total == MAX_NUMBER_OF_RESULT || pageNumber > 1) {
			// On regarde si y'en a pas d'autres
			total = users.getTotalUsers(userNameOrEmail, userId, onlyNonFriend);
			if (total > MAX_NUMBER_OF_RESULT) {
				int last = 0;
				List<Page> pages = new ArrayList<Page>();
				for (int i = 0; i < total / MAX_NUMBER_OF_RESULT; i++) {
					String num = (i + 1) + "";
					pages.add(new Page(num));
					last = i + 1;
				}
				if (total % MAX_NUMBER_OF_RESULT != 0) {
					last = pages.size() + 1;
					String num = last + "";
					pages.add(new Page(num));
				}

				request.setAttribute("pages", pages);
				request.setAttribute("last", last);
			}
		}

		List<User> friends = userRelations.getAllUsersInRelation(userId);
		if (!onlyNonFriend) {
			for (User user : foundUsers) {
				user.isInMyNetwork = friends.contains(user);
			}
		}

		for (User user : foundUsers) {
			if (userRelationRequests.associationExists(userId, user.id)) {
				user.freeComment = "Vous avez déjà envoyé une demande à " + user.getName();
			}
		}

		request.setAttribute("current", pageNumber);
		request.setAttribute("users", foundUsers);
		request.setAttribute("name", userNameOrEmail);
		request.setAttribute("onlyNonFriend", onlyNonFriend);

		RootingsUtils.rootToPage(formUrl, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		doTheLogic(request, response);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		doTheLogic(req, resp);
	}

}
