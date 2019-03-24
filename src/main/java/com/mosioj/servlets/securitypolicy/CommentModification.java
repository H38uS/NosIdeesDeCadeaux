package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.model.table.Comments;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class CommentModification extends AllAccessToPostAndGet implements SecurityPolicy, CommentSecurityChecker {
	
	/**
	 * Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	private final String commentParameter;

	private final Comments comments;
	private Comment comment;

	/**
	 * 
	 * @param comments
	 * @param commentParameter Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	public CommentModification(Comments comments, String commentParameter) {
		this.comments = comments;
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

		Integer commentId = ParametersUtils.readInt(request, commentParameter);
		if (commentId == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getConnectedUser(request).id;

		comment = comments.getComment(commentId);
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
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		return canModifyIdea(request, response);
	}

	@Override
	public Comment getComment() {
		return comment;
	}

}
