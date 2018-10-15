package com.mosioj.servlets.controllers.compte;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.model.UserParameter;
import com.mosioj.notifications.NotificationActivation;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mon_compte")
public class MonCompte extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -101081965549681889L;
	private static final Logger logger = LogManager.getLogger(MonCompte.class);

	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
	public static final String URL = "/protected/mon_compte";

	private static File filePath;

	public MonCompte() {
		super(new AllAccessToPostAndGet());
	}
	
	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse resp) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		User current = users.getUser(userId);
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
		
		List<UserParameter> userNotificationParameters = userParameters.getUserNotificationParameters(userId);
		request.setAttribute("notif_types", userNotificationParameters);
		
		request.setAttribute("parents", parentRelationship.getParents(userId));
		request.setAttribute("children", parentRelationship.getChildren(userId));

		request.setAttribute("possible_values", NotificationActivation.values());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			if (filePath == null) {
				filePath = new File(getServletContext().getInitParameter("work_dir"), "uploaded_pictures/avatars");
				logger.info(MessageFormat.format("Setting file path to: {0}", filePath.getAbsolutePath()));
				filePath.mkdirs();
			}

			readMultiFormParameters(request, filePath);
			int userId = ParametersUtils.getUserId(request);
			
			CompteInteractions helper = new CompteInteractions();
			List<String> errors = helper.processSave(filePath, parameters, userId);
			if (errors == null || errors.isEmpty()) {
				request.getSession().setAttribute("sauvegarde_ok", true);
				User user = users.getUser(userId);
				request.getSession().setAttribute("emailorname", user.getName());
			} else {
				request.getSession().setAttribute("errors_info_gen", errors);
			}

		}

		RootingsUtils.redirectToPage(URL, request, response);
	}
	
}
