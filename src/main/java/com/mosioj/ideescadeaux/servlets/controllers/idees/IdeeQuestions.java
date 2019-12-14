package com.mosioj.ideescadeaux.servlets.controllers.idees;

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

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/idee_questions")
public class IdeeQuestions extends IdeesCadeauxGetAndPostServlet<CanAskReplyToQuestions> {

	private static final Logger logger = LogManager.getLogger(IdeeQuestions.class);

	private static final long serialVersionUID = -433226623397937479L;
	public static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/idee_questions.jsp";
	public static final String WEB_SERVLET = "/protected/idee_questions";

	public IdeeQuestions() {
		super(new CanAskReplyToQuestions(IDEA_ID_PARAM));
	}

	/**
	 * Drops all notification linked to questions of the given owner links to the given idea.
	 * 
	 * @param owner
	 * @param ideaId
	 * @throws SQLException
	 */
	private void dropNotificationOnView(User owner, int ideaId) throws SQLException {
		model.notif.removeAllType(owner, NotificationType.IDEA_ADDED_BY_FRIEND, ParameterName.IDEA_ID, ideaId);
		model.notif.removeAllType(owner, NotificationType.NEW_QUESTION_ON_IDEA, ParameterName.IDEA_ID, ideaId);
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		Idee idea = policy.getIdea();
		request.setAttribute("idee", idea);
		request.setAttribute("isOwner", idea.owner == thisOne);
		request.setAttribute("comments", model.questions.getCommentsOn(idea.getId()));
		dropNotificationOnView(thisOne, idea.getId());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();
		logger.info(MessageFormat.format("Ajout d''une question sur l''idée {0}...", idea.getId()));
		String text = ParametersUtils.readAndEscape(request, "text");

		User current = thisOne;
		model.questions.addComment(current.id, idea.getId(), text);

		Set<User> toBeNotified = new HashSet<User>();

		// If the idea is booked, we notify the bookers
		toBeNotified.addAll(idea.getBookers(model.groupForIdea, model.sousReservation));

		// Notifying at least all people in the thread
		toBeNotified.addAll(model.questions.getUserListOnComment(idea.getId()));

		// Faut que le owner soit au courant des questions :)
		toBeNotified.add(idea.owner);

		// Removing current user, and notifying others
		toBeNotified.remove(current);
		logger.debug(MessageFormat.format("Personnes à prévenir : {0}", toBeNotified));
		for (User notified : toBeNotified) {
			model.notif.addNotification(notified.id, new NotifNewQuestionOnIdea(current, idea, idea.owner.equals(notified)));
		}

		dropNotificationOnView(thisOne, idea.getId());
		RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + idea.getId(), request, response);
	}

}