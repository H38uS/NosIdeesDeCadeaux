package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Optional;

/**
 * A policy to make sure we can interact with an idea : forbids the owner of the idea.
 *
 * @author Jordan Mosio
 */
public final class CanAskReplyToQuestions extends SecurityPolicy implements IdeaSecurityChecker {

    private static final Logger logger = LogManager.getLogger(CanAskReplyToQuestions.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    private final String ideaParameter;

    private Idee idea;

    /**
     * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    public CanAskReplyToQuestions(String ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param request  The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canInteractWithIdea(HttpServletRequest request) throws SQLException {

        Optional<Integer> ideaId = ParametersUtils.readInt(request, ideaParameter);
        if (!ideaId.isPresent()) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        Optional<Idee> tentative = IdeesRepository.getIdeaWithoutEnrichment(ideaId.get());
        if (!tentative.isPresent()) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        idea = tentative.get();
        if (idea.getSurpriseBy() != null) {
            lastReason = "Vous ne pouvez pas poser de question car il s'agit d'une surprise... ;)";
            return false;
        }

        int userId = connectedUser.id;
        boolean res = UserRelationsRepository.associationExists(userId, idea.owner.id);
        if (!res) {
            lastReason = "Vous n'avez pas accès aux idées de cette personne.";
        }

        return res || userId == idea.owner.id;

    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithIdea(request);
        } catch (SQLException e) {
            logger.error("Got an exception while checking security: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canInteractWithIdea(request);
        } catch (SQLException e) {
            logger.error("Got an exception while checking security: " + e.getMessage());
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