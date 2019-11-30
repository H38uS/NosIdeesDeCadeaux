package com.mosioj.ideescadeaux.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.model.table.IsUpToDateQuestions;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/confirmation_est_a_jour")
public class ConfirmationEstAJour extends AbstractIdea<IdeaModification> {

	private static final long serialVersionUID = -6645017540948612364L;
	public static final String IDEE_FIELD_PARAMETER = "idee";

	public ConfirmationEstAJour() {
		super(new IdeaModification(IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();
		model.idees.touch(idea.getId());
		int userId = thisOne.id;
		new IsUpToDateQuestions().deleteAssociation(idea.getId(), userId);
		
		List<AbstractNotification> notifications = model.notif.getNotification(ParameterName.IDEA_ID, idea.getId());
		Set<Integer> ids = new HashSet<>();
		for (AbstractNotification notification : notifications) {
			if (notification instanceof NotifAskIfIsUpToDate) {
				NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
				model.notif.addNotification(	isUpToDate.getUserIdParam(),
				                      	new NotifConfirmedUpToDate(model.users.getUser(userId), idea));
				model.notif.remove(notification.id);
				ids.add(isUpToDate.getUserIdParam());
			}
		}
		for (AbstractNotification notification : notifications) {
			// Deletes old confirmation notifications
			if (notification instanceof NotifConfirmedUpToDate) {
				NotifConfirmedUpToDate confirmed = (NotifConfirmedUpToDate) notification;
				if (ids.contains(confirmed.owner)) {
					model.notif.remove(notification.id);
				}
			}
		}

		RootingsUtils.rootToPage(MesNotifications.URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesNotifications.URL, request, response);
	}

}
