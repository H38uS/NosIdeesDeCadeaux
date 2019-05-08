package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.utils.NotLoggedInException;

/**
 * A basic policy that allow all connection to the POST URL.
 * @author Jordan Mosio
 *
 */
public class AllAccessToPostAndGet extends SecurityPolicy {

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return true;
	}

	@Override
	public boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return true;
	}

	@Override
	public boolean isPostRequestAllowed() {
		return true;
	}

}
