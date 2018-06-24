package com.mosioj.servlets.controllers.idees;

import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;

public abstract class AbstractUserListes extends AbstractListes<User> {

	private static final long serialVersionUID = 1638868138216657989L;

	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	public AbstractUserListes(SecurityPolicy policy) {
		super(policy, 6);
	}

	@Override
	protected String getViewPageURL() {
		return VIEW_PAGE_URL;
	}
}
