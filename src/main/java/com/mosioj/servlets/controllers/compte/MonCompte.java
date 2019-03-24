package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.model.UserParameter;
import com.mosioj.notifications.NotificationActivation;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mon_compte")
public class MonCompte extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -101081965549681889L;
	private static final Logger logger = LogManager.getLogger(MonCompte.class);

	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
	public static final String URL = "/protected/mon_compte";

	public MonCompte() {
		super(new AllAccessToPostAndGet());
	}
	
	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		logger.debug("Displaying mon compte page...");
		User current = ParametersUtils.getConnectedUser(request);
		request.setAttribute("user", current);

		HttpSession session = request.getSession();
		if (session.getAttribute("sauvegarde_ok") != null) {
			request.setAttribute("sauvegarde_ok", session.getAttribute("sauvegarde_ok"));
			session.removeAttribute("sauvegarde_ok");
		}
		if (session.getAttribute("errors_info_gen") != null) {
			request.setAttribute("errors_info_gen", session.getAttribute("errors_info_gen"));
			session.removeAttribute("errors_info_gen");
		}
		
		List<UserParameter> userNotificationParameters = userParameters.getUserNotificationParameters(current.id);
		request.setAttribute("notif_types", userNotificationParameters);
		
		request.setAttribute("parents", parentRelationship.getParents(current.id));
		request.setAttribute("children", parentRelationship.getChildren(current.id));

		request.setAttribute("possible_values", NotificationActivation.values());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(URL, request, response);
	}
	
}
