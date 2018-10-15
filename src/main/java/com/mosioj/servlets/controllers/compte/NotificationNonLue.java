package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.NotificationModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/notification_non_lue")
public class NotificationNonLue extends IdeesCadeauxServlet<NotificationModification> {

	private static final long serialVersionUID = -5381776220247069645L;
	private static final String NOTIFICATION_PARAMETER = "notif_id";

	public NotificationNonLue() {
		super(new NotificationModification(notif, NOTIFICATION_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		notif.setUnread(ParametersUtils.readInt(req, NOTIFICATION_PARAMETER));
		RootingsUtils.redirectToPage(MyNotifications.URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MyNotifications.URL, request, response);
	}

}
