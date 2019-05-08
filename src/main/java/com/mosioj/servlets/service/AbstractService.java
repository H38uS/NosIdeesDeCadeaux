package com.mosioj.servlets.service;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.viewhelper.JSonResponseWriter;
import com.mosioj.viewhelper.JSonResponseWriter.JSonPair;

public abstract class AbstractService<P extends SecurityPolicy> extends IdeesCadeauxServlet<P> {

	private static final long serialVersionUID = 3014602524272535511L;
	protected final JSonResponseWriter writter = new JSonResponseWriter();

	public AbstractService(P policy) {
		super(policy);
	}

	protected JSonPair makeJSonPair(String key, String value) {
		return writter.makeJSonPair(key, value);
	}
}
