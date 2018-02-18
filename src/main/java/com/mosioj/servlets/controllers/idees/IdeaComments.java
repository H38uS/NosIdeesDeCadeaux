package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.Share;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifNewQuestionOnIdea;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/idee_commentaires")
public class IdeaComments extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -433226623397937479L;
	public static final String IDEA_ID_PARAM = "idee";
	public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";
	public static final String URL = "/protected/idee_commentaires";

	public IdeaComments() {
		super(new IdeaInteraction(userRelations, idees, IDEA_ID_PARAM));
	}

	private void insertMandatoryParams(HttpServletRequest req, Integer id) throws SQLException {
		Idee idea = idees.getIdea(id);
		req.setAttribute("text", idea.getText());
		req.setAttribute("idee", id);
		req.setAttribute("comments", comments.getCommentsOn(id));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Integer id = ParametersUtils.readInt(req, IDEA_ID_PARAM);
		insertMandatoryParams(req, id);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, IDEA_ID_PARAM);
		String text = ParametersUtils.readAndEscape(request, "text");

		int userId = ParametersUtils.getUserId(request);
		comments.saveComment(userId, id, text);
		Idee idea = idees.getIdea(id);

		Set<User> toBeNotified = new HashSet<User>();

		// If the idea is booked, we notify the bookers
		User current = users.getUser(userId);
		if (idea.isBooked()) {
			User bookingOwner = idea.getBookingOwner();
			if (bookingOwner != null) {
				toBeNotified.add(bookingOwner);
			} else {
				// Idea is booked by a group
				int groupId = idea.getGroupKDO();
				for (Share share : groupForIdea.getGroupDetails(groupId).getShares()) {
					toBeNotified.add(share.getUser());
				}
			}
		}

		// Notifying at least all people in the thread
		toBeNotified.addAll(questions.getUserListOnComment(idea.getId()));
		
		// Removing current user, and notifying others
		toBeNotified.remove(current);
		for (User notified : toBeNotified) {
			notif.addNotification(notified.id, new NotifNewQuestionOnIdea(current, idea));
		}

		insertMandatoryParams(request, id);
		request.setAttribute("success", true);
		RootingsUtils.redirectToPage(MessageFormat.format("{0}?{1}={2}", URL, IDEA_ID_PARAM, id), request, response);
	}

}
