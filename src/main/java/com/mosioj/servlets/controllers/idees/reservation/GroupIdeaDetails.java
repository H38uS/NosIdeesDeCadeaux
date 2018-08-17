package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.IdeaGroup;
import com.mosioj.model.Share;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifGroupSuggestion;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.database.NoRowsException;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/detail_du_groupe")
public class GroupIdeaDetails extends AbstractIdea {

	private static final long serialVersionUID = -2188278918134412556L;
	private static final Logger logger = LogManager.getLogger(GroupIdeaDetails.class);

	public static final String GROUP_ID_PARAM = "groupid";

	public static final String VIEW_PAGE_URL = "/protected/detail_du_groupe.jsp";
	public static final String GET_PAGE_WITH_GROUP_ID = "/protected/detail_du_groupe?groupid=";

	/**
	 * Class constructor.
	 */
	public GroupIdeaDetails() {
		super(new BookingGroupInteraction(userRelations, idees, GROUP_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int groupId = ParametersUtils.readInt(request, GROUP_ID_PARAM);

		logger.debug("Getting details for idea group " + groupId + "...");
		IdeaGroup group = groupForIdea.getGroupDetails(groupId);
		double currentTotal = 0;
		for (Share share : group.getShares()) {
			currentTotal += share.getAmount();
		}
		double remaining = group.getTotal() - currentTotal;


		Object errors = request.getSession().getAttribute("errors");
		if (errors != null) {
			request.setAttribute("errors", errors);
			request.getSession().removeAttribute("errors");
		}
		
		try {
			request.setAttribute("idee", idees.getIdea(idees.getIdeaId(groupId)));
		} catch (NoRowsException e) {
			RootingsUtils.rootToUnexistingGroupError(request, response);
			return;
		}
		request.setAttribute("is_in_group", groupForIdea.belongsToGroup(ParametersUtils.getUserId(request), groupId));
		request.setAttribute("group", group);
		request.setAttribute("currentTotal", currentTotal);
		request.setAttribute("remaining", String.format("%1$,.2f", remaining));
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);

	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		Integer groupId = ParametersUtils.readInt(request, GROUP_ID_PARAM);
		String amount = ParametersUtils.readIt(request, "amount").replaceAll(",", ".");

		if ("annulation".equals(amount)) {
			groupForIdea.removeUserFromGroup(userId, groupId);
			List<AbstractNotification> notifications = notif.getNotification(ParameterName.GROUP_ID, groupId);
			for (AbstractNotification notification : notifications) {
				notif.remove(notification.id);
			}
			RootingsUtils.redirectToPage(GET_PAGE_WITH_GROUP_ID + groupId, request, response);
			return;
		}

		ParameterValidator val = ValidatorFactory.getMascValidator(amount, "montant");
		val.checkEmpty();
		val.checkIfAmount();
		val.checkIntegerGreaterThan(1);
		List<String> errorsAmount = val.getErrors();

		if (!errorsAmount.isEmpty()) {
			request.getSession().setAttribute("errors", errorsAmount);
		} else {
			// Modification de la participation
			boolean newMember = groupForIdea.updateAmount(groupId, userId, Double.parseDouble(amount));
			if (newMember) {
				List<AbstractNotification> notifications = notif.getNotification(ParameterName.GROUP_ID, groupId);
				for (AbstractNotification notification : notifications) {
					if (notification instanceof NotifGroupSuggestion && notification.owner == userId) {
						notif.remove(notification.id);
					}
				}
			}
		}

		RootingsUtils.redirectToPage(GET_PAGE_WITH_GROUP_ID + groupId, request, response);
	}

}
