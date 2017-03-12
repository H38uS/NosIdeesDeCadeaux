package com.mosioj.servlets.securitypolicy;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Idee;
import com.mosioj.model.table.Idees;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaModification extends AllAccessToPostAndGet implements SecurityPolicy {

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

		Integer ideaId = ParametersUtils.readInt(request, ideaParameter);
		if (ideaId == null) {
			lastReason = "Aucune idée trouvée en paramètre.";
			return false;
		}
		
		int userId = ParametersUtils.getUserId(request);

		try {

			Idee idea = idees.getIdea(ideaId);
			if (idea == null) {
				lastReason = "Aucune idée trouvée en paramètre.";
				return false;
			}
			
			boolean res = userId == idea.owner.id;
			if (!res) {
				lastReason = "Vous ne pouvez modifier que vos idées.";
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
		return canModifyIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		return canModifyIdea(request, response);
	}

}
