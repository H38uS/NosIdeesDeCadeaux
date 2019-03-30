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
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/suggerer_relations")
public class SuggererRelations extends AbstractListes<User, NetworkAccess> {

	private static final long serialVersionUID = -5480617244868517709L;
	private static final String USER_PARAMETER = "id";
	private static final String DISPATCH_URL = "/protected/suggerer_relations.jsp";

	public SuggererRelations() {
		super(new NetworkAccess(USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		int suggestTo = ParametersUtils.readInt(req, USER_PARAMETER);

		req.setAttribute("user", model.users.getUser(suggestTo));

		RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int suggestTo = ParametersUtils.readInt(request, USER_PARAMETER);
		User suggestToUser = model.users.getUser(suggestTo);
		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();

		int suggestedBy = thisOne.id;
		List<User> toBeSuggested = model.userRelations.getAllUsersInRelationNotInOtherNetwork(suggestedBy,
																						suggestTo,
																						userNameOrEmail,
																						0,
																						maxNumberOfResults);
		toBeSuggested.remove(suggestToUser);

		for (User u : toBeSuggested) {
			if (model.userRelationsSuggestion.hasReceivedSuggestionOf(suggestTo, u.id)) {
				u.freeComment = MessageFormat.format("{0} a déjà reçu une suggestion pour {1}.", suggestToUser.getName(), u.getName());
			}
			if (model.userRelationRequests.associationExists(suggestTo, u.id)) {
				u.freeComment = MessageFormat.format("{0} a déjà envoyé une demande à {1}.", suggestToUser.getName(), u.getName());
			}
			if (model.userRelationRequests.associationExists(u.id, suggestTo)) {
				u.freeComment = MessageFormat.format("{0} a déjà envoyé une demande à {1}.", u.getName(), suggestToUser.getName());
			}
		}

		request.setAttribute("name", userNameOrEmail);
		request.setAttribute("user", suggestToUser);
		request.setAttribute("users", toBeSuggested);

		RootingsUtils.rootToPage(DISPATCH_URL, request, response);
	}

	@Override
	protected String getViewPageURL() {
		return null;
	}

	@Override
	protected String getCallingURL() {
		return null;
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return null;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return 0;
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
		return null;
	}

}
