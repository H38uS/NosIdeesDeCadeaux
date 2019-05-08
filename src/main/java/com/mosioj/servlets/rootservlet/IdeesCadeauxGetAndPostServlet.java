package com.mosioj.servlets.rootservlet;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyPost;

public abstract class IdeesCadeauxGetAndPostServlet<P extends SecurityPolicyOnlyPost> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = -1513319177739695079L;

	public IdeesCadeauxGetAndPostServlet(P policy) {
		super(policy);
	}

	// FIXME : 0 utiliser l'un des trois pour toutes les servlets
}
