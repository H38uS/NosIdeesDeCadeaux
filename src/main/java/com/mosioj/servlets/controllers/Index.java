package com.mosioj.servlets.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/index")
public class Index extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	public static final int NB_DAYS_MAX_BEFORE_BIRTHDAY = 20;
	private static final long serialVersionUID = -8386214705432810179L;
	private static final String VIEW_URL = "/protected/index.jsp";

	public Index() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		User me = users.getUser(ParametersUtils.getUserId(req));
		req.setAttribute("no_birth_date_set", me.getBirthday() == null);

		List<User> friends = userRelations.getCloseBirthday(ParametersUtils.getUserId(req), NB_DAYS_MAX_BEFORE_BIRTHDAY);
		req.setAttribute("userBirthday", friends);
		RootingsUtils.rootToPage(VIEW_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
