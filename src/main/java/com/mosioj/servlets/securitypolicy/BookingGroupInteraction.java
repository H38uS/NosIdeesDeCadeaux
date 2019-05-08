package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.utils.NotLoggedInException;

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
	
	private Integer groupId;

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

		Optional<Integer> groupIdParam = readInt(request, groupParameter);
		if (!groupIdParam.isPresent()) {
			lastReason = "Aucun groupe trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;

		User ideaOwner = model.idees.getIdeaOwnerFromGroup(groupIdParam.get());
		if (ideaOwner == null) {
			lastReason = "Ce groupe appartient à personne.";
			return false;
		}

		if (!model.userRelations.associationExists(userId, ideaOwner.id)) {
			lastReason = "Vous n'avez pas accès aux idées de cette personne.";
			return false;
		}

		groupId = groupIdParam.get();
		return true;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithGroup(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canInteractWithGroup(request, response);
	}

	/**
	 * 
	 * @return The resolved group id, or null if the checks failed.
	 */
	public Integer getGroupId() {
		return groupId;
	}
}
