package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.table.Idees;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaModification extends AllAccessToPostAndGet implements SecurityPolicy, IdeaSecurityChecker {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	private final Idees idees;
	private Idee idea;

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
	 * @throws SQLException
	 * @throws NotLoggedInException 
	 */
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {

		Integer ideaId = ParametersUtils.readInt(request, ideaParameter);
		if (ideaId == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		idea = idees.getIdea(ideaId);
		if (idea == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}

		boolean res = userId == idea.owner.id;
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos idées.";
		}
		return res;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyIdea(request, response);
	}

	@Override
	public Idee getIdea() {
		return idea;
	}

}
