package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaInteraction extends AllAccessToPostAndGet implements IdeaSecurityChecker {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	protected Idee idea;

	/**
	 * 
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	public IdeaInteraction(String ideaParameter) {
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

		Optional<Integer> ideaId = ParametersUtils.readInt(request, ideaParameter);
		if (!ideaId.isPresent()) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}

		int userId = connectedUser.id;

		idea = model.idees.getIdeaWithoutEnrichment(ideaId.get());
		if (idea == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}

		boolean res = model.userRelations.associationExists(userId, idea.owner.id);
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
