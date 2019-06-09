package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.servlets.securitypolicy.NotificationModification;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/notification_lue")
public class NotificationLue extends IdeesCadeauxGetServlet<NotificationModification> {

	private static final long serialVersionUID = -5381776220247069645L;
	private static final String NOTIFICATION_PARAMETER = "notif_id";

	public NotificationLue() {
		super(new NotificationModification(NOTIFICATION_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		model.notif.setRead(policy.getNotificationId());
		RootingsUtils.redirectToPage(MesNotifications.URL, req, resp);
	}

}
