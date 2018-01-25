package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.CommentModification;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_commentaire")
public class SupprimerCommentaire extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 7722016569684838786L;
	private static final String COMMENT_ID_PARAMETER = "id";

	public SupprimerCommentaire() {
		super(new CommentModification(comments, COMMENT_ID_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Integer id = ParametersUtils.readInt(req, COMMENT_ID_PARAMETER);
		Comment comment = getCommentFromSecurityChecks();
		comments.delete(id);

		RootingsUtils.rootToPage(	MessageFormat.format(	"{0}?{1}={2}",
																IdeaComments.URL,
																IdeaComments.IDEA_ID_PARAM,
																comment.getIdea()),
										req,
										resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
