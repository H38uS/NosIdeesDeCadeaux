package com.mosioj.ideescadeaux.webapp.servlets.rootservlet;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;

public abstract class IdeesCadeauxPostServlet<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxPostServlet(P policy) {
		super(policy);
	}

	@Override
	public final void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		throw new ServletException("Method not supported");
	}

}
