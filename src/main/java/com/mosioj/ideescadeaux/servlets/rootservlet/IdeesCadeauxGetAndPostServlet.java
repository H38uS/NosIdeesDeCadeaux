package com.mosioj.ideescadeaux.servlets.rootservlet;

import com.mosioj.ideescadeaux.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

public abstract class IdeesCadeauxGetAndPostServlet<P extends SecurityPolicy> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxGetAndPostServlet(P policy) {
		super(policy);
	}
}
