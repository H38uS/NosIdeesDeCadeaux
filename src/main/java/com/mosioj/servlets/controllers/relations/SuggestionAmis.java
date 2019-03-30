package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.instance.NotifNewRelationSuggestion;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/suggestion_amis")
public class SuggestionAmis extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -8566629037022016825L;
	private static final String DISPATCH_URL = "suggestion_amis.jsp";

	public SuggestionAmis() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		req.setAttribute("suggestions", model.userRelationsSuggestion.getUserSuggestions(thisOne));
		RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = thisOne.id;

		Map<String, String[]> params = request.getParameterMap();

		List<Integer> toBeAsked = getSelectedChoices(params, "selected_");
		List<Integer> toIgnore = getSelectedChoices(params, "reject_");
		
		List<String> errors = new ArrayList<String>();

		for (int userToAsk : toBeAsked) {
			
			User userToSendInvitation = model.users.getUser(userToAsk);
			model.userRelationsSuggestion.removeIfExists(userId, userToAsk);
			
			if (userToSendInvitation.id == userId || model.userRelations.associationExists(userToSendInvitation.id, userId)) {
				errors.add(MessageFormat.format("{0} fait déjà parti de votre réseau.", userToSendInvitation.getName()));
				continue;
			}
			
			if (model.userRelationRequests.associationExists(userId, userToSendInvitation.id)) {
				errors.add(MessageFormat.format("Vous avez déjà envoyé une demande à {0}.", userToSendInvitation.getName()));
				continue;
			}
			
			// On ajoute l'association
			model.userRelationRequests.insert(thisOne, userToSendInvitation);
		}
		
		for (int ignore : toIgnore) {
			model.userRelationsSuggestion.removeIfExists(userId, ignore);
		}

		List<AbstractNotification> notifications = model.notif.getUserNotifications(userId);
		for (AbstractNotification n : notifications) {
			if (n instanceof NotifNewRelationSuggestion) {
				NotifNewRelationSuggestion notification = (NotifNewRelationSuggestion) n;
				if (!model.userRelationsSuggestion.hasReceivedSuggestionFrom(userId, notification.getUserIdParam())) {
					model.notif.remove(notification.id);
				}
			}
		}
		
		request.setAttribute("suggestions", model.userRelationsSuggestion.getUserSuggestions(thisOne));
		request.setAttribute("error_messages", errors);
		RootingsUtils.rootToPage(DISPATCH_URL, request, response);
	}

}
