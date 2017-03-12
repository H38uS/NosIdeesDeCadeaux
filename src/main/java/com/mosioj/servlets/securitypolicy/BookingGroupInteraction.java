package com.mosioj.servlets.securitypolicy;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

/**
 * A policy to make sure we can interact with a group.
 * 
 * @author Jordan Mosio
 *
 */
public class BookingGroupInteraction extends AllAccessToPostAndGet implements SecurityPolicy {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String groupParameter;

	private final UserRelations userRelations;
	private final Idees idees;

	/**
	 * 
	 * @param userRelations
	 * @param idees
	 * @param groupParameter Defines the string used in HttpServletRequest to retrieve the group id.
	 */
	public BookingGroupInteraction(UserRelations userRelations, Idees idees, String groupParameter) {
		this.userRelations = userRelations;
		this.idees = idees;
		this.groupParameter = groupParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 */
	private boolean canInteractWithGroup(HttpServletRequest request, HttpServletResponse response) {

		Integer groupId = ParametersUtils.readInt(request, groupParameter);
		if (groupId == null) {
			lastReason = "Aucun groupe trouvé en paramètre.";
			return false;
		}
		
		int userId = ParametersUtils.getUserId(request);

		try {

			User ideaOwner = idees.getIdeaOwnerFromGroup(groupId);
			if (ideaOwner == null) {
				lastReason = "Ce groupe appartient à personne.";
				return false;
			}
			
			boolean res = userRelations.associationExists(userId, ideaOwner.id);
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

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		return canInteractWithGroup(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		return canInteractWithGroup(request, response);
	}

}
