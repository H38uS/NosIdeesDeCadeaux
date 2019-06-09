package com.mosioj.servlets.securitypolicy.generic;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyGet;
import com.mosioj.utils.NotLoggedInException;

public class AllAccessToGet extends SecurityPolicyOnlyGet {

	@Override
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return true;
	}

}
