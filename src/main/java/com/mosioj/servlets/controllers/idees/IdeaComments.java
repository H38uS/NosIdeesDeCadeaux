package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/idee_commentaires")
public class IdeaComments extends IdeesCadeauxServlet<IdeaInteraction> {

	private static final long serialVersionUID = -433226623397937479L;
	public static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";
	public static final String WEB_SERVLET = "/protected/idee_commentaires";

	public IdeaComments() {
		super(new IdeaInteraction(userRelations, idees, IDEA_ID_PARAM));
	}

	/**
	 * Drops all notification linked to questions of the given owner links to the given idea.
	 * 
	 * @param ownerId
	 * @param ideaId
	 * @throws SQLException
	 */
	private void dropNotificationOnView(int ownerId, int ideaId) throws SQLException {
		notif.removeAllType(ownerId, NotificationType.NEW_COMMENT_ON_IDEA, ParameterName.IDEA_ID, ideaId);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		request.setAttribute("idee", idea);
		request.setAttribute("comments", comments.getCommentsOn(idea.getId()));
		dropNotificationOnView(ParametersUtils.getUserId(request), idea.getId());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		String text = ParametersUtils.readAndEscape(request, "text");

		int userId = ParametersUtils.getUserId(request);
		comments.addComment(userId, id, text);
		Idee idea = policy.getIdea();

		Set<User> toBeNotified = new HashSet<User>();

		// If the idea is booked, we notify the bookers
		User current = users.getUser(userId);
		toBeNotified.addAll(idea.getBookers(groupForIdea, sousReservation));

		// Notifying at least all people in the thread
		toBeNotified.addAll(comments.getUserListOnComment(idea.getId()));

		// Removing current user, and notifying others
		toBeNotified.remove(current);

		for (User notified : toBeNotified) {
			notif.addNotification(notified.id, new NotifNewCommentOnIdea(current, idea));
		}

		dropNotificationOnView(ParametersUtils.getUserId(request), id);
		RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + id, request, response);
	}

}
