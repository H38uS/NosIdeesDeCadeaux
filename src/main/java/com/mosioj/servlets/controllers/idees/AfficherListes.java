package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends AbstractListes {

	private static final long serialVersionUID = 1209953017190072617L;

	public static final String AFFICHER_LISTES = "/protected/afficher_listes";
	private static final String NAME_OR_EMAIL = "name";

	/**
	 * Class constructor.
	 * 
	 */
	public AfficherListes() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	protected List<User> getDisplayedUsers(int userId, int firstRow, HttpServletRequest req) throws SQLException {
		String nameOrEmail = ParametersUtils.readAndEscape(req, NAME_OR_EMAIL);
		List<User> ids = new ArrayList<User>();
		int MAX = MAX_NUMBER_OF_RESULT;
		User connected = users.getUser(userId);
		if (connected.matchNameOrEmail(nameOrEmail)) {
			ids.add(connected);
			MAX--;
		}
		ids.addAll(userRelations.getAllUsersInRelation(userId, nameOrEmail, firstRow, MAX));
		return ids;
	}

	@Override
	protected int getTotalNumberOfUsers(int userId, HttpServletRequest req) throws SQLException {
		String nameOrEmail = ParametersUtils.readAndEscape(req, NAME_OR_EMAIL);
		int size = userRelations.getAllUsersInRelation(userId, nameOrEmail).size();
		User connected = users.getUser(userId);
		if (connected.matchNameOrEmail(nameOrEmail)) {
			return size + 1;
		}
		return size;
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(	MessageFormat.format(	"{0}?{1}={2}",
																AFFICHER_LISTES,
																NAME_OR_EMAIL,
																ParametersUtils.readIt(request, NAME_OR_EMAIL)),
										request,
										response); // Rien de spécifique pour le moment
	}

	@Override
	protected String getCallingURL() {
		return AFFICHER_LISTES.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return MessageFormat.format("&{0}={1}", NAME_OR_EMAIL, ParametersUtils.readAndEscape(req, NAME_OR_EMAIL));
	}

}
