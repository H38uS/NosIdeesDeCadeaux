package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A policy to make sure we can reply to questions on this idea : allows the owner of the idea (if not a surprise).
 *
 * @author Jordan Mosio
 */
public final class CanAskReplyToQuestions extends SecurityPolicy implements IdeaSecurityChecker {

    /** Defines the string used in HttpServletRequest to retrieve the idea id. */
    private final String ideaParameter;

    private Idee idea;

    /**
     * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    public CanAskReplyToQuestions(String ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canInteractWithIdea(HttpServletRequest request) {

        idea = ParametersUtils.readInt(request, ideaParameter)
                              .flatMap(IdeesRepository::getIdea)
                              .orElse(null);
        if (idea == null) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        if (idea.getSurpriseBy().isPresent()) {
            lastReason = "Vous ne pouvez pas poser de question car il s'agit d'une surprise... ;)";
            return false;
        }

        int userId = connectedUser.id;

        // Le owner peut toujours
        if (userId == idea.owner.id) {
            return true;
        }

        // Enfin, uniquement si on est amis
        if (!UserRelationsRepository.associationExists(connectedUser, idea.owner)) {
            lastReason = "Vous n'avez pas accès aux idées de cette personne.";
            return false;
        }
        return true;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return canInteractWithIdea(request);
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return canInteractWithIdea(request);
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
