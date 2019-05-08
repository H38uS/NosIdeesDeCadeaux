package com.mosioj.servlets.securitypolicy.root;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.utils.NotLoggedInException;

public abstract class SecurityPolicyOnlyPost extends SecurityPolicy {

	@Override
	public final boolean hasRightToInteractInGetRequest(HttpServletRequest request,
														HttpServletResponse response) throws SQLException, NotLoggedInException {
		return false;
	}

	@Override
	public final boolean isGetRequestAllowed() {
		return false;
	}

	@Override
	public final boolean isPostRequestAllowed() {
		return true;
	}

}
