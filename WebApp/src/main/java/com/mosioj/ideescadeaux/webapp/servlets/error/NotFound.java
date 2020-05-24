package com.mosioj.ideescadeaux.webapp.servlets.error;

import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.NotLoggedInException;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/public/NotFound")
public class NotFound extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 936404523785343564L;
	private static final String VIEW_PROTECTED_URL = "/protected/NotFound.jsp";
	private static final String VIEW_PUBLIC_URL = "/public/NotFound.jsp";

	public NotFound() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		try {
			RootingsUtils.rootToPage(VIEW_PROTECTED_URL, request, response);
		} catch (NotLoggedInException e) {
			RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
		}
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
