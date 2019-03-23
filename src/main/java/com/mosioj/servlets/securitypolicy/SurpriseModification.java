package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.UserRelations;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 * 
 * @author Jordan Mosio
 *
 */
public class SurpriseModification extends AllAccessToPostAndGet implements SecurityPolicy, IdeaSecurityChecker {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	private final UserRelations userRelations;
	private final Idees idees;

	protected Idee idea;

	/**
	 * 
	 * @param userRelations
	 * @param idees
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	public SurpriseModification(UserRelations userRelations, Idees idees, String ideaParameter) {
		this.userRelations = userRelations;
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
	protected boolean canInteractWithIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {

		Integer ideaId = ParametersUtils.readInt(request, ideaParameter);
		if (ideaId == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		idea = idees.getIdeaWithoutEnrichment(ideaId);
		if (idea == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}
		
		if (idea.getSurpriseBy() == null || idea.getSurpriseBy().id != userId) {
			lastReason = "Vous n'avez pas créé cette surprise.";
			return false;
		}

		boolean res = userRelations.associationExists(userId, idea.owner.id);
		if (!res) {
			lastReason = "Vous n'avez pas accès aux idées de cette personne.";
		}
		return res;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithIdea(request, response);
	}

	@Override
	public Idee getIdea() {
		return idea;
	}

}
