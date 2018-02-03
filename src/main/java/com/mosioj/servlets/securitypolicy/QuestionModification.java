package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.model.table.Questions;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.utils.ParametersUtils;

public class QuestionModification extends AllAccessToPostAndGet implements SecurityPolicy, CommentSecurityChecker {
	
	/**
	 * Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	private final String questionParameter;

	private final Questions questions;
	private Comment comment;

	/**
	 * 
	 * @param questions
	 * @param questionParameter Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	public QuestionModification(Questions questions, String questionParameter) {
		this.questions = questions;
		this.questionParameter = questionParameter;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return True if the current user can interact with the idea.
	 * @throws SQLException
	 */
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Integer commentId = ParametersUtils.readInt(request, questionParameter);
		if (commentId == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		int userId = ParametersUtils.getUserId(request);

		comment = questions.getComment(commentId);
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
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyIdea(request, response);
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		return canModifyIdea(request, response);
	}

	@Override
	public Comment getComment() {
		return comment;
	}

}