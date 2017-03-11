package com.mosioj.servlets.securitypolicy;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Idees;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaModification implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	private final Idees idees;

	/**
	 * 
	 * @param idees
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	public IdeaModification(Idees idees, String ideaParameter) {
		this.idees = idees;
		this.ideaParameter = ideaParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 */
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) {

		Integer idea = ParametersUtils.readInt(request, ideaParameter);
		if (idea == null) {
			return false;
		}
		
		int userId = ParametersUtils.getUserId(request);

		try {

			return userId == idees.getIdea(idea).owner.id;

		} catch (SQLException e) {
			try {
				RootingsUtils.rootToGenericSQLError(e, request, response);
			} catch (ServletException | IOException e1) {
				// Nothing to do
			}
			return false;
		}
	}

	// TODO : pouvoir directement accéder à l'idée
	
	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		return canModifyIdea(request, response);
	}

	@Override
	public boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		return canModifyIdea(request, response);
	}

	@Override
	public boolean isPostRequestAllowed() {
		return true;
	}

}
