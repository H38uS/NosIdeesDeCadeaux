package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

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
	 * @throws SQLException
	 * @throws NotLoggedInException 
	 */
	private boolean canInteractWithGroup(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {

		Integer groupId = ParametersUtils.readInt(request, groupParameter);
		if (groupId == null) {
			lastReason = "Aucun groupe trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getConnectedUser(request).id;

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
