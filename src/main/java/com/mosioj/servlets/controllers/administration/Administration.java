package com.mosioj.servlets.controllers.administration;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/administration/administration")
public class Administration extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 1944117196491457908L;
	private static final Logger logger = LogManager.getLogger(Administration.class);

	public static final String DISPATCH_URL = "/administration/administration.jsp";

	/**
	 * Class constructor.
	 */
	public Administration() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		logger.info("Getting administration page from user: " + ParametersUtils.getConnectedUser(request));
		List<User> allUsers = users.getAllUsers();
		request.setAttribute("users", allUsers);
		RootingsUtils.rootToPage(DISPATCH_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
