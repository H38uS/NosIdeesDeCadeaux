package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends AbstractListes<User> {

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
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
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
		return MessageFormat.format("&{0}={1}&{2}={3}",
									"name",
									ParametersUtils.readAndEscape(request, "name").trim(),
									"only_non_friend",
									ParametersUtils.readAndEscape(request, "only_non_friend").trim());
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest request) throws SQLException {
		int userId = ParametersUtils.getUserId(request);
		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
		return users.getTotalUsers(userNameOrEmail, userId, onlyNonFriend);
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest request) throws SQLException {

		int userId = ParametersUtils.getUserId(request);

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
		boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
		List<User> foundUsers = users.getUsers(userNameOrEmail, userId, onlyNonFriend, firstRow, maxNumberOfResults);

		if (!onlyNonFriend) {
			for (User user : foundUsers) {
				user.isInMyNetwork = userRelations.associationExists(user.id, userId);
			}
		}

		for (User user : foundUsers) {
			if (userRelationRequests.associationExists(userId, user.id)) {
				user.freeComment = "Vous avez déjà envoyé une demande à " + user.getName();
			}
		}

		return foundUsers;
	}

}
