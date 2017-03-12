package com.mosioj.servlets.securitypolicy;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Idees;
import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaInteraction extends AllAccessToPostAndGet implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	private final UserRelations userRelations;
	private final Idees idees;

	/**
	 * 
	 * @param userRelations
	 * @param idees
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	public IdeaInteraction(UserRelations userRelations, Idees idees, String ideaParameter) {
		this.userRelations = userRelations;
		this.idees = idees;
		this.ideaParameter = ideaParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 */
	private boolean canInteractWithIdea(HttpServletRequest request, HttpServletResponse response) {

		Integer idea = ParametersUtils.readInt(request, ideaParameter);
		if (idea == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}
		
		int userId = ParametersUtils.getUserId(request);

		try {

			boolean res = userRelations.associationExists(userId, idees.getIdea(idea).owner.id);
			if (!res) {
				lastReason = "Vous n'avez pas accès aux idées de cette personne.";
			}
			return res;

		} catch (SQLException e) {
			try {
				RootingsUtils.rootToGenericSQLError(e, request, response);
			} catch (ServletException | IOException e1) {
				// Nothing to do
			}
			lastReason = "Une erreur est survenue pendant votre demande. Veuillez réessayer.";
			return false;
		}
	}

	// TODO : pouvoir directement accéder à l'idée
	
	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		return canInteractWithIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		return canInteractWithIdea(request, response);
	}

}
