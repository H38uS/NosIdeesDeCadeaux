package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.servlets.securitypolicy.CommentModification;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_commentaire")
public class SupprimerCommentaire extends IdeesCadeauxGetServlet<CommentModification> {

	private static final long serialVersionUID = 7722016569684838786L;
	private static final String COMMENT_ID_PARAMETER = "id";

	public SupprimerCommentaire() {
		super(new CommentModification(COMMENT_ID_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Comment comment = policy.getComment();
		model.comments.delete(comment.getId());
		RootingsUtils.rootToPage(IdeaComments.WEB_SERVLET + "?" + IdeaComments.IDEA_ID_PARAM + "=" + comment.getIdea(), req, resp);
	}

}
