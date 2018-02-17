package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends AbstractUserListes {

	private static final long serialVersionUID = 1209953017190072617L;
	private static final Logger logger = LogManager.getLogger(AfficherListes.class);

	public static final String AFFICHER_LISTES = "/protected/afficher_listes";
	private static final String NAME_OR_EMAIL = "name";

	/**
	 * Class constructor.
	 * 
	 */
	public AfficherListes() {
		super(new AllAccessToPostAndGet());
	}

	/**
	 * 
	 * @param req
	 * @return The String to pass to the database
	 */
	private String getEffectiveParameter(HttpServletRequest req) {

		String nameOrEmail = ParametersUtils.readAndEscape(req, NAME_OR_EMAIL);
		logger.trace(MessageFormat.format("Receive:{0}", nameOrEmail));

		if (nameOrEmail == null || nameOrEmail.trim().isEmpty()) {
			return nameOrEmail;
		}

		int open = nameOrEmail.indexOf("(");
		int close = nameOrEmail.indexOf(")");
		if (open > 0 && close > 0 && open < close) {
			// Comes from some completion trick
			nameOrEmail = nameOrEmail.substring(open + 1, close);
		}

		logger.trace(MessageFormat.format("Returned:{0}", nameOrEmail.trim()));
		return nameOrEmail.trim();
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {
		int userId = ParametersUtils.getUserId(req);
		String nameOrEmail = getEffectiveParameter(req);
		List<User> ids = new ArrayList<User>();
		int MAX = maxNumberOfResults;
		User connected = users.getUser(userId);
		if (connected.matchNameOrEmail(nameOrEmail)) {
			ids.add(connected);
			MAX--;
		}
		ids.addAll(userRelations.getAllUsersInRelation(userId, nameOrEmail, firstRow, MAX));
		fillsUserIdeas(userId, ids);
		return ids;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException, NotLoggedInException {
		int userId = ParametersUtils.getUserId(req);
		String nameOrEmail = getEffectiveParameter(req);
		int size = userRelations.getAllUsersInRelationCount(userId, nameOrEmail);
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
																request.getParameter(NAME_OR_EMAIL)),
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
