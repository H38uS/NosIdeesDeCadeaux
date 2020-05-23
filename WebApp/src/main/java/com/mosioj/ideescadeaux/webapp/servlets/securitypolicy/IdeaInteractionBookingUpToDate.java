package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * A policy to make sure we can interact with the idea, but not ask if up to date if it's  a surprise!
 *
 * @author Jordan Mosio
 */
public final class IdeaInteractionBookingUpToDate extends IdeaInteraction {

    public IdeaInteractionBookingUpToDate(String ideaParameter) {
        super(ideaParameter);
    }

    /**
     * @param request  The http request.
     * @param response The http response.
     * @return True if the current user can interact with the idea.
     */
    protected boolean canInteractWithIdea(HttpServletRequest request,
                                          HttpServletResponse response) throws SQLException {
        if (super.canInteractWithIdea(request, response)) {
            if (idea.getSurpriseBy().isPresent()) {
                lastReason = "Impossible de réserver / demander des nouvelles sur cette idée... Il s'agit d'une surprise !";
                return false;
            }
            return true;
        }
        return false;
    }

}
