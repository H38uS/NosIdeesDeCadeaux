package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SecurityPolicy {

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 * @throws SQLException 
	 */
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException;

	/**
	 * 
	 * @return True if and only if GET are allowed on this context.
	 */
	public boolean isGetRequestAllowed();

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 * @throws SQLException 
	 */
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException;

	/**
	 * 
	 * @return True if and only if POST are allowed on this context.
	 */
	public boolean isPostRequestAllowed();

	/**
	 * 
	 * @return The last reason for a forbidden access.
	 */
	public String getLastReason();
}