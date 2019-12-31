package com.mosioj.ideescadeaux.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewCommentOnIdea;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/idee_commentaires")
public class IdeaComments extends IdeesCadeauxGetAndPostServlet<IdeaInteraction> {

	private static final long serialVersionUID = -433226623397937479L;
	public static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";
	public static final String WEB_SERVLET = "/protected/idee_commentaires";

	public IdeaComments() {
		super(new IdeaInteraction(IDEA_ID_PARAM));
	}

	/**
	 * Drops all notification linked to questions of the given owner links to the given idea.
	 * 
	 * @param owner
	 * @param ideaId
	 * @throws SQLException
	 */
	private void dropNotificationOnView(User owner, int ideaId) throws SQLException {
		model.notif.removeAllType(owner, NotificationType.NEW_COMMENT_ON_IDEA, ParameterName.IDEA_ID, ideaId);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		request.setAttribute("idee", idea);
		request.setAttribute("comments", model.comments.getCommentsOn(idea.getId()));
		dropNotificationOnView(thisOne, idea.getId());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String text = ParametersUtils.readAndEscape(request, "text");

		User current = thisOne;
		Idee idea = policy.getIdea();
		model.comments.addComment(current.id, idea.getId(), text);

		Set<User> toBeNotified = new HashSet<User>();

		// If the idea is booked, we notify the bookers
		toBeNotified.addAll(idea.getBookers(model.sousReservation));

		// Notifying at least all people in the thread
		toBeNotified.addAll(model.comments.getUserListOnComment(idea.getId()));

		// Removing current user, and notifying others
		toBeNotified.remove(current);

		for (User notified : toBeNotified) {
			model.notif.addNotification(notified.id, new NotifNewCommentOnIdea(current, idea));
		}

		dropNotificationOnView(thisOne, idea.getId());
		RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + idea.getId(), request, response);
	}

}
