package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class QuestionModification extends AllAccessToPostAndGet implements CommentSecurityChecker {
	
	/**
	 * Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	private final String questionParameter;

	private Comment comment;

	/**
	 * 
	 * @param questionParameter Defines the string used in HttpServletRequest to retrieve the comment id.
	 */
	public QuestionModification(String questionParameter) {
		this.questionParameter = questionParameter;
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

		Integer commentId = ParametersUtils.readInt(request, questionParameter);
		if (commentId == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		User thisOne = connectedUser;

		comment = model.questions.getComment(commentId);
		if (comment == null) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		boolean res = thisOne == comment.getWrittenBy();
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
