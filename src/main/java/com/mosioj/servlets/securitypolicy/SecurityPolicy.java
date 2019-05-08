package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.logichelpers.ModelAccessor;
import com.mosioj.utils.NotLoggedInException;

public abstract class SecurityPolicy {
	
	protected final ModelAccessor model = new ModelAccessor();
	protected String lastReason = "";
	protected User connectedUser;

	/**
	 * Sets up a new connected user. May be null.
	 * 
	 * @param user
	 */
	public void setConnectedUser(User user) {
		connectedUser = user;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 * @throws SQLException 
	 * @throws NotLoggedInException 
	 */
	public abstract boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException;

	/**
	 * 
	 * @return True if and only if GET are allowed on this context.
	 */
	public abstract boolean isGetRequestAllowed();

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 * @throws SQLException 
	 * @throws NotLoggedInException 
	 */
	public abstract boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException;

	/**
	 * 
	 * @return True if and only if POST are allowed on this context.
	 */
	public abstract boolean isPostRequestAllowed();

	/**
	 * 
	 * @return The last reason for a forbidden access.
	 */
	public String getLastReason() {
		return lastReason;
	}
}
