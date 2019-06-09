package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.IdeaGroup;
import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/suggerer_groupe_idee")
public class SuggestGroupIdea extends IdeesCadeauxGetAndPostServlet<BookingGroupInteraction> {

	private static final Logger logger = LogManager.getLogger(SuggestGroupIdea.class);
	private static final long serialVersionUID = 5094570058900475394L;
	public static final String GROUP_ID_PARAM = "groupid";
	public static final String VIEW_URL = "/protected/suggerer_groupe_idee";
	public static final String VIEW_PAGE_URL = "/protected/suggest_group_idea.jsp";

	/**
	 * Class constructor.
	 * 
	 */
	public SuggestGroupIdea() {
		super(new BookingGroupInteraction(GROUP_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int groupId = policy.getGroupId();
		logger.debug("Getting details for idea group " + groupId + "...");

		IdeaGroup group = model.groupForIdea.getGroupDetails(groupId).orElse(new IdeaGroup(-1, 0));

		Idee idee = model.idees.getIdeaWithoutEnrichmentFromGroup(groupId);
		User user = thisOne;
		model.idees.fillAUserIdea(user, idee, device);

		List<User> potentialGroupUser = model.idees.getPotentialGroupUser(groupId, user.id);
		logger.debug(MessageFormat.format("Potential users: {0}", potentialGroupUser));
		List<User> removable = new ArrayList<User>();
		for (User toRemove : potentialGroupUser) {
			NotifGroupSuggestion suggestion = new NotifGroupSuggestion(user, groupId, idee);
			if (model.notif.hasNotification(toRemove.id, suggestion)) {
				removable.add(toRemove);
			}
		}
		potentialGroupUser.removeAll(removable);

		request.setAttribute("candidates", potentialGroupUser);
		request.setAttribute("idee", idee);
		request.setAttribute("group", group);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int groupId = policy.getGroupId();
		Idee idee = model.idees.getIdeaWithoutEnrichmentFromGroup(groupId);
		model.idees.fillAUserIdea(thisOne, idee, device);

		List<Integer> selectedUsers = new ArrayList<Integer>();
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			if (values.length == 1 && "on".equals(values[0])) {
				try {
					int user = Integer.parseInt(key);
					selectedUsers.add(user);
				} catch (NumberFormatException nfe) {
				}
			}
		}

		List<User> successTo = new ArrayList<User>();

		logger.debug("Selected users : " + selectedUsers);
		for (int userId : selectedUsers) {
			User user = model.users.getUser(userId);
			NotifGroupSuggestion suggestion = new NotifGroupSuggestion(thisOne, groupId, idee);
			if (!model.notif.hasNotification(userId, suggestion)) {
				model.notif.addNotification(userId, suggestion);
			}
			successTo.add(user);
		}

		request.setAttribute("sent_to_users", successTo);
		ideesKDoGET(request, response);
	}

}
