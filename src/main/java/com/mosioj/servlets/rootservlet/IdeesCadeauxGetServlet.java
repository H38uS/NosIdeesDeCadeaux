package com.mosioj.servlets.rootservlet;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyGet;

public abstract class IdeesCadeauxGetServlet<P extends SecurityPolicyOnlyGet> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxGetServlet(P policy) {
		super(policy);
	}

}
