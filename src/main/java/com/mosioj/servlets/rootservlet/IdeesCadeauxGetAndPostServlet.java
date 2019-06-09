package com.mosioj.servlets.rootservlet;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyGetAndPost;

public abstract class IdeesCadeauxGetAndPostServlet<P extends SecurityPolicyGetAndPost> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxGetAndPostServlet(P policy) {
		super(policy);
	}
}
