package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.ChildAdministration;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/connexion_enfant")
public class ConnexionEnfant extends DefaultCompte {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(ConnexionEnfant.class);
	private static final String CHILD_ID_PARAM = "name";
	public static final String VIEW_PAGE_URL = "/protected/child_success.jsp";

	public ConnexionEnfant() {
		super(new ChildAdministration(parentRelationship, CHILD_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int currentUserId = ParametersUtils.getUserId(request);
		Integer childId = ParametersUtils.readInt(request, CHILD_ID_PARAM);
		User current = users.getUser(currentUserId);
		User newOne = users.getUser(childId);

		logger.info(MessageFormat.format("Connection depuis {0} en tant que {1}.", currentUserId, childId));
		HttpSession session = request.getSession();
		session.setAttribute("initial_user_id", currentUserId);
		request.setAttribute("initial_user_name", current.getName());
		session.setAttribute("userid", childId);
		session.setAttribute("emailorname", newOne.getName());
		request.setAttribute("emailorname", newOne.getName());

		request.setAttribute("new_name", newOne.getName());
		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

}
