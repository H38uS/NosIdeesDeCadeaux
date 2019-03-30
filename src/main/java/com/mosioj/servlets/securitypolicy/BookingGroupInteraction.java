package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

/**
 * A policy to make sure we can interact with a group.
 * 
 * @author Jordan Mosio
 *
 */
public class BookingGroupInteraction extends AllAccessToPostAndGet  {

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String groupParameter;

	/**
	 * 
	 * @param groupParameter Defines the string used in HttpServletRequest to retrieve the group id.
	 */
	public BookingGroupInteraction(String groupParameter) {
		this.groupParameter = groupParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 * @throws NotLoggedInException 
	 */
	private boolean canInteractWithGroup(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {

		Integer groupId = ParametersUtils.readInt(request, groupParameter);
		if (groupId == null) {
			lastReason = "Aucun groupe trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;

		User ideaOwner = model.idees.getIdeaOwnerFromGroup(groupId);
		if (ideaOwner == null) {
			lastReason = "Ce groupe appartient à personne.";
			return false;
		}

		boolean res = model.userRelations.associationExists(userId, ideaOwner.id);
		if (!res) {
			lastReason = "Vous n'avez pas accès aux idées de cette personne.";
		}
		return res;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithGroup(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithGroup(request, response);
	}

}
