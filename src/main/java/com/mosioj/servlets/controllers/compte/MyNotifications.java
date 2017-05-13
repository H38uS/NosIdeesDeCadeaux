package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.notifications.AbstractNotification;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mes_notifications")
public class MyNotifications extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -5988235378202921870L;
	public static final String URL = "/protected/mes_notifications";
	private static final String VIEW_URL = "/protected/mes_notifications.jsp";

	public MyNotifications() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		List<AbstractNotification> notifications = notif.getUserNotifications(ParametersUtils.getUserId(req));
		req.setAttribute("notifications", notifications);
		RootingsUtils.rootToPage(VIEW_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
