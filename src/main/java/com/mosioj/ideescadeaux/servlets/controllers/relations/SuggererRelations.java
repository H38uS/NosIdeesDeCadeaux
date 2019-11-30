package com.mosioj.ideescadeaux.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/suggerer_relations")
public class SuggererRelations extends IdeesCadeauxGetAndPostServlet<NetworkAccess> {

	private static final long serialVersionUID = -5480617244868517709L;
	private static final String USER_PARAMETER = "id";
	private static final String DISPATCH_URL = "/protected/suggerer_relations.jsp";

	public SuggererRelations() {
		super(new NetworkAccess(USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		req.setAttribute("user", policy.getUser());
		RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User suggestToUser = policy.getUser();
		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();

		int suggestedBy = thisOne.id;
		List<User> toBeSuggested = model.userRelations.getAllUsersInRelationNotInOtherNetwork(	suggestedBy,
																								suggestToUser.id,
																								userNameOrEmail,
																								0,
																								50);
		toBeSuggested.remove(suggestToUser);

		for (User u : toBeSuggested) {
			if (model.userRelationsSuggestion.hasReceivedSuggestionOf(suggestToUser.id, u.id)) {
				u.freeComment = MessageFormat.format("{0} a déjà reçu une suggestion pour {1}.", suggestToUser.getName(), u.getName());
			}
			if (model.userRelationRequests.associationExists(suggestToUser.id, u.id)) {
				u.freeComment = MessageFormat.format("{0} a déjà envoyé une demande à {1}.", suggestToUser.getName(), u.getName());
			}
			if (model.userRelationRequests.associationExists(u.id, suggestToUser.id)) {
				u.freeComment = MessageFormat.format("{0} a déjà envoyé une demande à {1}.", u.getName(), suggestToUser.getName());
			}
		}

		request.setAttribute("name", userNameOrEmail);
		request.setAttribute("user", suggestToUser);
		request.setAttribute("users", toBeSuggested);

		RootingsUtils.rootToPage(DISPATCH_URL, request, response);
	}

}
