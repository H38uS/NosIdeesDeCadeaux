package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.servlets.controllers.compte.MyNotifications;
import com.mosioj.servlets.securitypolicy.IdeaModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/confirmation_est_a_jour")
public class ConfirmationEstAJour extends AbstractIdea {

	private static final long serialVersionUID = -6645017540948612364L;
	public static final String IDEE_FIELD_PARAMETER = "idee";

	public ConfirmationEstAJour() {
		super(new IdeaModification(idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(req, IDEE_FIELD_PARAMETER);
		idees.touch(id);

		Idee idea = idees.getIdea(id);
		List<AbstractNotification> notifications = notif.getNotification(ParameterName.IDEA_ID, id);
		for (AbstractNotification notification : notifications) {
			if (notification instanceof NotifAskIfIsUpToDate) {
				NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
				notif.addNotification(	isUpToDate.getUserIdParam(),
				                      	new NotifConfirmedUpToDate(users.getUser(ParametersUtils.getUserId(req)), idea));
				notif.remove(notification.id);
			}
		}

		RootingsUtils.redirectToPage(MyNotifications.URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MyNotifications.URL, request, response);
	}

}
