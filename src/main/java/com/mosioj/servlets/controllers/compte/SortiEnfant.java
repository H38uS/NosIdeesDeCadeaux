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
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/sorti_enfant")
public class SortiEnfant extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(SortiEnfant.class);
	public static final String VIEW_PAGE_URL = "/protected/child_exit_success.jsp";

	public SortiEnfant() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// Récupération des variables
		int childId = ParametersUtils.getUserId(request);
		HttpSession session = request.getSession();
		Object initial = session.getAttribute("initial_user_id");
		if (initial == null) {
			RootingsUtils.redirectToPage(MonCompte.URL, request, response);
			return;
		}
		Integer initialAccountId = null;
		try {
			initialAccountId = Integer.parseInt(initial.toString());
		} catch (Exception e) {
			RootingsUtils.redirectToPage(MonCompte.URL, request, response);
			return;
		}
		
		User childAccount = users.getUser(childId);
		User oldOne = users.getUser(initialAccountId);
		
		logger.info(MessageFormat.format("Retour à {0} depuis {1}.", initialAccountId, childId));
		session.removeAttribute("initial_user_id");
		session.removeAttribute("initial_user_name");
		session.setAttribute("userid", initialAccountId);
		session.setAttribute("emailorname", oldOne.getName());
		request.setAttribute("emailorname", oldOne.getName());
		request.setAttribute("old_name", childAccount.getName());
		request.removeAttribute("initial_user_name");

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}
