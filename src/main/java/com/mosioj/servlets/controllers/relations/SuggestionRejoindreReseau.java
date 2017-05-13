package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class SuggestionRejoindreReseau extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 5529157183937072477L;
	private static final String USER_PARAMETER = "userId";
	private static final String URL_SUCCESS = "suggerer_relations_succes.jsp";
	private static final String URL_ERROR = "suggerer_relations_error.jsp";

	public SuggestionRejoindreReseau() {
		super(new NetworkAccess(userRelations, USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		RootingsUtils.rootToPage("/protected", req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int suggestedBy = ParametersUtils.getUserId(request);
		int suggestTo = ParametersUtils.readInt(request, USER_PARAMETER);
		String start = "selected_";

		List<Integer> suggestedUsers = new ArrayList<Integer>();
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			// TODO faire une méthode utile pour récupérer cela
			String[] values = params.get(key);
			if (values.length == 1 && "on".equals(values[0])) {
				String id = key.substring(start.length());
				try {
					suggestedUsers.add(Integer.parseInt(id));
				} catch (NumberFormatException nfe) {
				}
			}
		}

		// Persist suggestion
		List<User> sent = new ArrayList<User>();
		for (int toBeAdded : suggestedUsers) {
			if (userRelationsSuggestion.newSuggestion(suggestedBy, suggestTo, toBeAdded)) {
				sent.add(users.getUser(toBeAdded));
			}
		}
		if (sent.size() > 0) {
			// Send a notification
			notif.addNotification(suggestTo, new NotifNewRelationSuggestion(suggestedBy, ParametersUtils.getUserName(request)));
			request.setAttribute("user", users.getUser(suggestTo));
			request.setAttribute("users", sent);
			RootingsUtils.rootToPage(URL_SUCCESS, request, response);
		} else {
			request.setAttribute("user", users.getUser(suggestTo));
			RootingsUtils.rootToPage(URL_ERROR, request, response);
		}
	}

}
