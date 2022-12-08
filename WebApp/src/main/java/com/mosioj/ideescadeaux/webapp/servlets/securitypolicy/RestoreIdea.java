package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * A policy to make sure we are allowed to modify this idea.
 *
 * @author Jordan Mosio
 */
public final class RestoreIdea extends SecurityPolicy implements IdeaSecurityChecker {

    /** Class Logger */
    private static final Logger logger = LogManager.getLogger(RestoreIdea.class);

    /** Defines the string used in HttpServletRequest to retrieve the idea id. */
    private final String ideaParameter;

    private Idee idea;

    /**
     * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    public RestoreIdea(String ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canModifyIdea(HttpServletRequest request) throws SQLException {

        idea = ParametersUtils.readInt(request, ideaParameter)
                              .flatMap(IdeesRepository::getDeletedIdea)
                              .orElse(null);
        if (idea == null) {
            lastReason = "Aucune idée trouvée en paramètre, ou elle n'existe pas.";
            return false;
        }

        // Si c'est une surprise, on ne peut que si c'est nous qui l'avons créée (et donc impossible que ce soit la nôtre)
        if (idea.isASurprise()) {
            if (idea.getSurpriseBy().map(connectedUser::equals).orElse(false)) {
                return true;
            } else {
                lastReason = "Impossible de restorer une surprise si ce n'est pas vous qui l'avez créée.";
                return false;
            }
        }

        boolean res = connectedUser.equals(idea.owner) ||
                      ParentRelationshipRepository.getChildren(connectedUser.id).contains(idea.owner);
        if (!res) {
            lastReason = "Vous ne pouvez restorer que vos idées ou celles de vos enfants.";
        }

        return res;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canModifyIdea(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canModifyIdea(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public Idee getIdea() {
        return idea;
    }

    @Override
    public void reset() {
        idea = null;
    }

}
