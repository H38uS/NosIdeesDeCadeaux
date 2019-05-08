package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.utils.NotLoggedInException;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 * 
 * @author Jordan Mosio
 *
 */
public final class IdeaInteractionBookingUpToDate extends IdeaInteraction {

	public IdeaInteractionBookingUpToDate(String ideaParameter) {
		super(ideaParameter);
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
		if (super.canInteractWithIdea(request, response)) {
			if (idea.getSurpriseBy() != null) {
				lastReason = "Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !";
				return false;
			}
			return true;
		}
		return true; 
		// FIXME : 0 truc pas logique... On peut réserver des surprises. Et pourquoi c'est true à la fin si super.canInteractWithIdea
		// est false ??
	}

}
