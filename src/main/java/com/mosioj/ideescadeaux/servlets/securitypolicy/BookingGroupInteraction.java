package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;

/**
 * A policy to make sure we can interact with a group.
 * 
 * @author Jordan Mosio
 *
 */
public final class BookingGroupInteraction extends SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(BookingGroupInteraction.class);

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
	private boolean canInteractWithGroup(HttpServletRequest request, HttpServletResponse response) throws SQLException {
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
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canInteractWithGroup(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canInteractWithGroup(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	/**
	 * 
	 * @return The resolved group id, or null if the checks failed.
	 */
	public Integer getGroupId() {
		return groupId;
	}
}
