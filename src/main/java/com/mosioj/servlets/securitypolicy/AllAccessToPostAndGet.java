package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A basic policy that allow all connection to the POST URL.
 * @author Jordan Mosio
 *
 */
public class AllAccessToPostAndGet implements SecurityPolicy {
	
	protected String lastReason = "";

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return true;
	}

	@Override
	public boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return true;
	}

	@Override
	public boolean isPostRequestAllowed() {
		return true;
	}

	@Override
	public String getLastReason() {
		return lastReason;
	}

}
