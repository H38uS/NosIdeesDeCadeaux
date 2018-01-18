package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Idees;
import com.mosioj.model.table.UserRelations;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 * 
 * @author Jordan Mosio
 *
 */
public class IdeaInteractionBookingUpToDate extends IdeaInteraction {

	public IdeaInteractionBookingUpToDate(UserRelations userRelations, Idees idees, String ideaParameter) {
		super(userRelations, idees, ideaParameter);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 */
	protected boolean canInteractWithIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		if (super.canInteractWithIdea(request, response)) {
			if (idea.getSurpriseBy() != null) {
				lastReason = "Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !";
				return false;
			}
			return true;
		}
		return true;
	}

}
