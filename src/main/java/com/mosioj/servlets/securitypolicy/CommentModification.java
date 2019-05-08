package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.servlets.securitypolicy.root.SecurityPolicyOnlyGet;
import com.mosioj.utils.NotLoggedInException;

public final class CommentModification extends SecurityPolicyOnlyGet implements CommentSecurityChecker {
	
	/**
	 * Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	private final String commentParameter;

	private Comment comment;

	/**
	 * 
	 * @param commentParameter Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	public CommentModification(String commentParameter) {
		this.commentParameter = commentParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 * @throws NotLoggedInException 
	 */
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {

		Optional<Integer> commentId = readInt(request, commentParameter);
		if (!commentId.isPresent()) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		int userId = connectedUser.id;

		comment = model.comments.getComment(commentId.get());
		if (comment == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		boolean res = userId == comment.getWrittenBy().id;
		if (!res) {
			lastReason = "Vous ne pouvez modifier que vos commentaires.";
		}
		return res;

	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyIdea(request, response);
	}

	@Override
	public Comment getComment() {
		return comment;
	}

}
