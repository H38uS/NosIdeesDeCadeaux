package com.mosioj.servlets.securitypolicy.root;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.utils.NotLoggedInException;

public class SecurityPolicyOnlyPost extends SecurityPolicy {

	@Override
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGetRequestAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPostRequestAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

}
