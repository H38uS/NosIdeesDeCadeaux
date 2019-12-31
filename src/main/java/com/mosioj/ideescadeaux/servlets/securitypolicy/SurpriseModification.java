package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 *
 * @author Jordan Mosio
 */
public final class SurpriseModification extends SecurityPolicy implements IdeaSecurityChecker {

    private static final Logger logger = LogManager.getLogger(SurpriseModification.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    private final String ideaParameter;

    protected Idee idea;

    /**
     * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    public SurpriseModification(String ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param request  The http request.
     * @return True if the current user can interact with the idea.
     */
    protected boolean canInteractWithIdea(HttpServletRequest request) throws SQLException {

        Optional<Integer> ideaId = ParametersUtils.readInt(request, ideaParameter);
        if (!ideaId.isPresent()) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        User thisOne = connectedUser;

        idea = IdeesRepository.getIdeaWithoutEnrichment(ideaId.get());
        if (idea == null) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        if (idea.getSurpriseBy() == null || !idea.getSurpriseBy().equals(thisOne)) {
            lastReason = "Vous n'avez pas créé cette surprise.";
            return false;
        }

        boolean res = UserRelationsRepository.associationExists(thisOne.id, idea.owner.id);
        if (!res) {
            lastReason = "Vous n'avez pas accès aux idées de cette personne.";
        }
        return res;

    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithIdea(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithIdea(request);
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
