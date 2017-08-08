package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/update_notification_parameter")
public class UpdateNotificationParameter extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -8614135676006947704L;

	public UpdateNotificationParameter() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		int userId = ParametersUtils.getUserId(request);
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		
		if (name != null) {
			userParameters.insertUpdateParameter(userId, name, value);
		}
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}