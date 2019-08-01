package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.table.ParentRelationship;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.utils.NotLoggedInException;

/**
 * A policy to make sure we can interact with an idea.
 * 
 * @author Jordan Mosio
 *
 */
public final class IdeaModification extends SecurityPolicy implements IdeaSecurityChecker {

	private static final Logger logger = LogManager.getLogger(IdeaModification.class);

	/**
	 * Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	private final String ideaParameter;

	private Idee idea;

	/**
	 * 
	 * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
	 */
	public IdeaModification(String ideaParameter) {
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
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Optional<Integer> ideaId = readInt(request, ideaParameter);
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

		boolean res = userId == idea.owner.id || new ParentRelationship().getChildren(userId).contains(idea.owner);
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos idées ou celles de vos enfants.";
		}
		return res;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canModifyIdea(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return canModifyIdea(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public Idee getIdea() {
		return idea;
	}

}
