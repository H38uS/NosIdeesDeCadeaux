package com.mosioj.ideescadeaux.servlets.securitypolicy.root;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.logichelpers.ModelAccessor;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

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
	 * @param name
	 * @return The parameter, as an integer. If it is not possible, returns null.
	 */
	protected Optional<Integer> readInt(HttpServletRequest request, String name) {
		try {
			return Optional.of(Integer.parseInt(ParametersUtils	.readIt(request, name)
																.replaceAll("[  ]", "")
																.replaceAll("%C2%A0", "")));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 */
	public abstract boolean hasRightToInteractInGetRequest(	HttpServletRequest request, HttpServletResponse response);

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if and only if the current connected user can perform a Get request with embedded parameters.
	 */
	public abstract boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response);


	/**
	 * 
	 * @return The last reason for a forbidden access.
	 */
	public String getLastReason() {
		return lastReason;
	}
}
