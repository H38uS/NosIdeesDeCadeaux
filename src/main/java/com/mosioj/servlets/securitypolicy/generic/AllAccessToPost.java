package com.mosioj.servlets.securitypolicy.generic;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyPost;
import com.mosioj.utils.NotLoggedInException;

public class AllAccessToPost extends SecurityPolicyOnlyPost {

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return true;
	}

}
