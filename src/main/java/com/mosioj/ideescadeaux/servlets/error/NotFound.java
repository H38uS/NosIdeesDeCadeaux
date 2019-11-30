package com.mosioj.ideescadeaux.servlets.error;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/public/NotFound")
public class NotFound extends IdeesCadeauxGetAndPostServlet<AllAccessToPostAndGet> {

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
