package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Comment;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.utils.NotLoggedInException;

public class QuestionModification extends SecurityPolicy implements CommentSecurityChecker {

	private static final Logger logger = LogManager.getLogger(QuestionModification.class);

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
	private boolean canModifyIdea(HttpServletRequest request, HttpServletResponse response) throws SQLException {

		Optional<Integer> commentId = readInt(request, questionParameter);
		if (!commentId.isPresent()) {
			lastReason = "Aucun commentaire trouvé en paramètre.";
			return false;
		}

		User thisOne = connectedUser;

		comment = model.questions.getComment(commentId.get());
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
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) {
		try {
			return canModifyIdea(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	final HttpServletRequest request,
													final HttpServletResponse response) {
		try {
			return canModifyIdea(request, response);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public Comment getComment() {
		return comment;
	}

}
