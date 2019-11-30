package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.viewhelper.JSonResponseWriter;
import com.mosioj.ideescadeaux.viewhelper.JSonResponseWriter.JSonPair;

public abstract class AbstractServicePost<P extends SecurityPolicy> extends IdeesCadeauxPostServlet<P> {

	private static final long serialVersionUID = 3014602524272535511L;
	protected final JSonResponseWriter writter = new JSonResponseWriter();

	public AbstractServicePost(P policy) {
		super(policy);
	}

	protected JSonPair makeJSonPair(String key, String value) {
		return writter.makeJSonPair(key, value);
	}
}
