package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/suggestion_rejoindre_reseau")
public class SuggestionRejoindreReseau extends IdeesCadeauxServlet<NetworkAccess> {

	private static final long serialVersionUID = 5529157183937072477L;
	private static final String USER_PARAMETER = "userId";
	private static final String URL_SUCCESS = "suggerer_relations_succes.jsp";
	private static final String URL_ERROR = "suggerer_relations_error.jsp";

	public SuggestionRejoindreReseau() {
		super(new NetworkAccess(USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		RootingsUtils.rootToPage("/protected", req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User suggestedBy = ParametersUtils.getConnectedUser(request);
		int suggestTo = ParametersUtils.readInt(request, USER_PARAMETER);

		List<Integer> suggestedUsers = getSelectedChoices(request.getParameterMap(), "selected_");

		// Persist suggestion
		List<User> sent = new ArrayList<User>();
		for (int toBeAdded : suggestedUsers) {
			if (model.userRelationsSuggestion.newSuggestion(suggestedBy.id, suggestTo, toBeAdded)) {
				sent.add(model.users.getUser(toBeAdded));
			}
		}
		if (sent.size() > 0) {
			// Send a notification
			model.notif.addNotification(suggestTo, new NotifNewRelationSuggestion(suggestedBy.id, suggestedBy.getName()));
			request.setAttribute("user", model.users.getUser(suggestTo));
			request.setAttribute("users", sent);
			RootingsUtils.rootToPage(URL_SUCCESS, request, response);
		} else {
			request.setAttribute("user", model.users.getUser(suggestTo));
			RootingsUtils.rootToPage(URL_ERROR, request, response);
		}
	}

}
