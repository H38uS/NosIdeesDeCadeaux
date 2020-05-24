package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A policy that checks modification rights for surprises.
 *
 * @author Jordan Mosio
 */
public final class SurpriseModification extends SecurityPolicy implements IdeaSecurityChecker {

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
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    protected boolean canInteractWithIdea(HttpServletRequest request) {

        idea = ParametersUtils.readInt(request, ideaParameter)
                              .flatMap(IdeesRepository::getIdea)
                              .orElse(null);
        if (idea == null) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        if (!connectedUser.equals(idea.getSurpriseBy().orElse(null))) {
            lastReason = "Vous n'avez pas créé cette surprise.";
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
