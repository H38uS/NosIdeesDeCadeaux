package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.text.Comment;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CommentModification extends SecurityPolicy implements CommentSecurityChecker {

    /**
     * Defines the string used in HttpServletRequest to retrieve the comment id.
     */
    private final String commentParameter;

    private Comment comment;

    /**
     * @param commentParameter Defines the string used in HttpServletRequest to retrieve the comment id.
     */
    public CommentModification(String commentParameter) {
        this.commentParameter = commentParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canModifyIdea(HttpServletRequest request) {

        comment = ParametersUtils.readInt(request, commentParameter)
                                 .flatMap(CommentsRepository::getComment)
                                 .orElse(null);

        if (comment == null) {
            lastReason = "Aucun commentaire trouvé en paramètre.";
            return false;
        }

        boolean res = connectedUser.equals(comment.getWrittenBy());
        if (!res) {
            lastReason = "Vous ne pouvez modifier que vos commentaires.";
        }

        return res;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return canModifyIdea(request);
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return canModifyIdea(request);
    }

    @Override
    public Comment getComment() {
        return comment;
    }

    @Override
    public void reset() {
        comment = null;
    }

}
