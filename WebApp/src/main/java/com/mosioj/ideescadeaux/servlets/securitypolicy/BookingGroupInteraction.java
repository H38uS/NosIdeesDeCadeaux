package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        Optional<Integer> groupIdParam = ParametersUtils.readInt(request, groupParameter);
        if (!groupIdParam.isPresent()) {
            lastReason = "Aucun groupe trouvé en paramètre.";
            return false;
        }

        int groupId = groupIdParam.get();
        int userId = connectedUser.id;

        User ideaOwner = IdeesRepository.getIdeaOwnerFromGroup(groupId);
        if (ideaOwner == null) {
            lastReason = "Ce groupe appartient à personne.";
            logger.warn("Un groupe n'appartient à aucune idée... => " + groupId);
            return false;
        }

        Optional<IdeaGroup> g = GroupIdeaRepository.getGroupDetails(groupId);
        if (!g.isPresent()) {
            lastReason = "Le groupe est introuvable...";
            logger.warn("Ce groupe n'existe pas... => " + groupId);
            return false;
        }

        if (!UserRelationsRepository.associationExists(userId, ideaOwner.id)) {
            lastReason = "Vous n'avez pas accès aux idées de cette personne.";
            return false;
        }

        theGroup = g.get();
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
    public IdeaGroup getGroupId() {
        return theGroup;
    }

    @Override
    public void reset() {
        theGroup = null;
    }

}
