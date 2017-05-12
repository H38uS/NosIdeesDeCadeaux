package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.model.table.Comments;
import com.mosioj.utils.ParametersUtils;

public class CommentModification extends AllAccessToPostAndGet implements SecurityPolicy {
	
	/**
	 * Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	private final String commentParameter;

	private final Comments comments;

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
	 */
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Integer commentId = ParametersUtils.readInt(request, commentParameter);
		if (commentId == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		Comment comment = comments.getComment(commentId);
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

	// TODO : pouvoir directement accéder au commentaire

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyIdea(request, response);
	}

}
