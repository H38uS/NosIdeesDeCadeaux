package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * A policy to make sure we can interact with a group.
 *
 * @author Jordan Mosio
 */
public final class BookingGroupInteraction extends SecurityPolicy {

    private static final Logger logger = LogManager.getLogger(BookingGroupInteraction.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    private final String groupParameter;

    private IdeaGroup theGroup;

    /**
     * @param groupParameter Defines the string used in HttpServletRequest to retrieve the group id.
     */
    public BookingGroupInteraction(String groupParameter) {
        this.groupParameter = groupParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     * @throws SQLException If an SQL error occurs.
     */
    private boolean canInteractWithGroup(HttpServletRequest request) throws SQLException {

        theGroup = ParametersUtils.readInt(request, groupParameter)
                                  .flatMap(GroupIdeaRepository::getGroupDetails)
                                  .orElse(null);
        if (theGroup == null) {
            lastReason = "Aucun groupe trouvé en paramètre.";
            return false;
        }

        // Le owner de l'idée sur laquelle existe ce groupe
        User ideaOwner = IdeesRepository.getIdeaOwnerFromGroup(theGroup).orElse(null);
        if (ideaOwner == null) {
            lastReason = "Ce groupe n'appartient à personne.";
            logger.error("Un groupe n'appartient à aucune idée... => " + theGroup.getId());
            return false;
        }

        if (!UserRelationsRepository.associationExists(connectedUser, ideaOwner)) {
            lastReason = "Vous n'avez pas accès aux idées de cette personne.";
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithGroup(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithGroup(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    /**
     * @return The resolved group id, or null if the checks failed.
     */
    public IdeaGroup getGroup() {
        return theGroup;
    }

    @Override
    public void reset() {
        theGroup = null;
    }

}
