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
		HttpSession session = request.getSession();
		Object initial = session.getAttribute("initial_connected_user");
		if (initial == null) {
			RootingsUtils.redirectToPage(MonCompte.URL, request, response);
			return;
		}
		
		User childAccount = thisOne;
		
		logger.info(MessageFormat.format("Retour à {0} depuis {1}.", initial, childAccount));
		session.removeAttribute("initial_connected_user");
		request.removeAttribute("initial_connected_user");

		session.setAttribute("connected_user", initial);
		request.setAttribute("connected_user", initial);
		request.setAttribute("old_name", childAccount.getName());

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}
