package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends AbstractListes<User, AllAccessToPostAndGet> {

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

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);

		request.setAttribute("name", userNameOrEmail);
		request.setAttribute("onlyNonFriend", onlyNonFriend);

		super.ideesKDoGET(request, response);
	}

	@Override
	protected String getViewPageURL() {
		return formUrl;
	}

	@Override
	protected String getCallingURL() {
		return "protected/rechercher_personne";
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("&name=");
		sb.append(ParametersUtils.readAndEscape(request, "name").trim());
		sb.append("&only_non_friend=");
		sb.append(ParametersUtils.readAndEscape(request, "only_non_friend").trim());
		return sb.toString();
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest request) throws SQLException, NotLoggedInException {
		int userId = thisOne.id;
		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
		return model.users.getTotalUsers(userNameOrEmail, userId, onlyNonFriend);
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest request) throws SQLException, NotLoggedInException {

		int userId = thisOne.id;

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
		List<User> foundUsers = model.users.getUsers(userNameOrEmail, userId, onlyNonFriend, firstRow, maxNumberOfResults);

		if (!onlyNonFriend) {
			for (User user : foundUsers) {
				user.isInMyNetwork = model.userRelations.associationExists(user.id, userId);
			}
		}

		for (User user : foundUsers) {
			if (model.userRelationRequests.associationExists(userId, user.id)) {
				user.freeComment = "Vous avez déjà envoyé une demande à " + user.getName();
			}
		}

		return foundUsers;
	}

}
