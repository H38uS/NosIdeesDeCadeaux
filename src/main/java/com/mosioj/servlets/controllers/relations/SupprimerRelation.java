package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifFriendshipDropped;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_relation")
public class SupprimerRelation extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 2491763819457048609L;
	private static final String USER_PARAMETER = "id";

	public SupprimerRelation() {
		super(new NetworkAccess(userRelations, USER_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer user = ParametersUtils.readInt(req, USER_PARAMETER);
		int currentId = ParametersUtils.getUserId(req);

		// Drops it
		userRelations.deleteAssociation(user, currentId);
		
		// Send a notification
		User me = users.getUser(currentId);
		notif.addNotification(user, new NotifFriendshipDropped(currentId, me.name));
		
		RootingsUtils.redirectToPage(AfficherReseau.URL + "?id=" + currentId, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(AfficherReseau.URL + "?id=" + ParametersUtils.getUserId(request), request, response);
	}

}
