package com.mosioj.servlets.rootservlet;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyPost;

public abstract class IdeesCadeauxPostServlet<P extends SecurityPolicyOnlyPost> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxPostServlet(P policy) {
		super(policy);
	}

}
