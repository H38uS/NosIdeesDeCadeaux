package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.repositories.IdeasWithInfoRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A policy to make sure we can get ideas - either ours if not a surprise, or the one from friends.
 *
 * @author Jordan Mosio
 */
public final class CanGetFriendsOrOwnNotSurpriseIdea extends SecurityPolicy implements IdeaSecurityChecker {

    /** Defines the string used in HttpServletRequest to retrieve the idea id. */
    private final String ideaParameter;

    private Idee idea;

    /**
     * @param ideaParameter Defines the string used in HttpServletRequest to retrieve the idea id.
     */
    public CanGetFriendsOrOwnNotSurpriseIdea(String ideaParameter) {
        this.ideaParameter = ideaParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canInteractWithIdea(HttpServletRequest request) {

        idea = ParametersUtils.readInt(request, ideaParameter)
                              .flatMap(IdeasWithInfoRepository::getIdea)
                              .orElse(null);
        if (idea == null) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        // Le owner peut si pas une surprise
        if (connectedUser.equals(idea.owner)) {
            if (idea.getSurpriseBy().isPresent()) {
                lastReason = "Non mais non... Où est le suspens ?";
                return false;
            }
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
