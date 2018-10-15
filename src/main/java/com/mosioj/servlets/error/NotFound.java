package com.mosioj.servlets.error;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

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
		if (ParametersUtils.getUserName(request) != null) {
			RootingsUtils.rootToPage(VIEW_PROTECTED_URL, request, response);
		} else {
			RootingsUtils.rootToPage(VIEW_PUBLIC_URL, request, response);
		}
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
