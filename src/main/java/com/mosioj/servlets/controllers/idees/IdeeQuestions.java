package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/idee_questions")
public class IdeeQuestions extends IdeesCadeauxServlet<CanAskReplyToQuestions> {

	private static final Logger logger = LogManager.getLogger(IdeeQuestions.class);

	private static final long serialVersionUID = -433226623397937479L;
	public static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/idee_questions.jsp";
	public static final String WEB_SERVLET = "/protected/idee_questions";

	public IdeeQuestions() {
		super(new CanAskReplyToQuestions(userRelations, idees, IDEA_ID_PARAM));
	}

	/**
	 * Drops all notification linked to questions of the given owner links to the given idea.
	 * 
	 * @param ownerId
	 * @param ideaId
	 * @throws SQLException
	 */
	private void dropNotificationOnView(int ownerId, int ideaId) throws SQLException {
		notif.removeAllType(ownerId, NotificationType.IDEA_ADDED_BY_FRIEND, ParameterName.IDEA_ID, ideaId);
		notif.removeAllType(ownerId, NotificationType.NEW_QUESTION_ON_IDEA, ParameterName.IDEA_ID, ideaId);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		request.setAttribute("idee", idea);
		request.setAttribute("isOwner", idea.owner.id == ParametersUtils.getUserId(request));
		request.setAttribute("comments", questions.getCommentsOn(idea.getId()));
		dropNotificationOnView(ParametersUtils.getUserId(request), idea.getId());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		logger.info(MessageFormat.format("Ajout d''une question sur l''idée {0}...", id));
		String text = ParametersUtils.readAndEscape(request, "text");

		int userId = ParametersUtils.getUserId(request);
		questions.addComment(userId, id, text);
		Idee idea = policy.getIdea();

		Set<User> toBeNotified = new HashSet<User>();

		// If the idea is booked, we notify the bookers
		User current = users.getUser(userId);
		toBeNotified.addAll(idea.getBookers(groupForIdea, sousReservation));

		// Notifying at least all people in the thread
		toBeNotified.addAll(questions.getUserListOnComment(idea.getId()));

		// Faut que le owner soit au courant des questions :)
		toBeNotified.add(idea.owner);

		// Removing current user, and notifying others
		toBeNotified.remove(current);
		logger.debug(MessageFormat.format("Personnes à prévenir : {0}", toBeNotified));
		for (User notified : toBeNotified) {
			notif.addNotification(notified.id, new NotifNewQuestionOnIdea(current, idea, idea.owner.equals(notified)));
		}

		dropNotificationOnView(ParametersUtils.getUserId(request), id);
		RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + id, request, response);
	}

}