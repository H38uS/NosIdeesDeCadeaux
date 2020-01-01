package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.core.model.database.NoRowsException;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    protected boolean canInteractWithIdea(HttpServletRequest request) {

        Optional<Integer> ideaId = ParametersUtils.readInt(request, ideaParameter);
        if (!ideaId.isPresent()) {
            lastReason = "Aucune idée trouvée en paramètre.";
            return false;
        }

        User thisOne = connectedUser;

        try {
            idea = IdeesRepository.getIdeaWithoutEnrichment(ideaId.get());
        } catch (NoRowsException e) {
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
