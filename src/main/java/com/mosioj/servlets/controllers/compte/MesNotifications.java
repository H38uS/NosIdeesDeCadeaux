package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.notifications.ChildNotifications;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mes_notifications")
public class MesNotifications extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final Logger logger = LogManager.getLogger(MesNotifications.class);
	
	private static final long serialVersionUID = -5988235378202921870L;
	public static final String URL = "/protected/mes_notifications";
	private static final String VIEW_URL = "/protected/mes_notifications.jsp";

	public MesNotifications() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		int userId = ParametersUtils.getUserId(req);
		req.setAttribute("unread_notifications", notif.getUserUnReadNotifications(userId));
		req.setAttribute("read_notifications", notif.getUserReadNotifications(userId));

		List<ChildNotifications> children = new ArrayList<ChildNotifications>();
		parentRelationship.getChildren(userId).forEach(c -> {
			try {
				children.add(new ChildNotifications(c, notif.getUserNotifications(c.id)));
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});
		req.setAttribute("child_notifications", children);

		RootingsUtils.rootToPage(VIEW_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}