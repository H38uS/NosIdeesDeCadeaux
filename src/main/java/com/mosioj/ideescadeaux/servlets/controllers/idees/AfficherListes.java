package com.mosioj.ideescadeaux.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends AbstractUserListes<AllAccessToPostAndGet> {

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
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {
		String nameOrEmail = readNameOrEmail(req, NAME_OR_EMAIL);
		List<User> ids = new ArrayList<User>();
		int MAX = maxNumberOfResults;
		User connected = thisOne;
		if (connected.matchNameOrEmail(nameOrEmail)) {
			ids.add(connected);
			MAX--;
		}
		ids.addAll(model.userRelations.getAllUsersInRelation(connected.id, nameOrEmail, firstRow, MAX));
		fillsUserIdeas(connected, ids);
		return ids;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest request) throws SQLException, NotLoggedInException {
		User user = thisOne;
		String nameOrEmail = readNameOrEmail(request, NAME_OR_EMAIL);
		int size = model.userRelations.getAllUsersInRelationCount(user, nameOrEmail);
		if (user.matchNameOrEmail(nameOrEmail)) {
			return size + 1;
		}
		return size;
	}

	@Override
	protected String getCallingURL() {
		return AFFICHER_LISTES.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		sb.append("&");
		sb.append(NAME_OR_EMAIL);
		sb.append("=");
		sb.append(ParametersUtils.readAndEscape(req, NAME_OR_EMAIL));
		return sb.toString();
	}

}
