package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/update_notification_parameter")
public class UpdateNotificationParameter extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = -8614135676006947704L;

	public UpdateNotificationParameter() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		User user = thisOne;
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		
		if (name != null && value != null) {
			model.userParameters.insertUpdateParameter(user, name, value);
		}
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}
