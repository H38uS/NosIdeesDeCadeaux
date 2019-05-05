package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Comment;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.QuestionModification;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_question")
public class SupprimerQuestions extends IdeesCadeauxServlet<QuestionModification> {

	private static final long serialVersionUID = 7722016569684838786L;
	private static final String COMMENT_ID_PARAMETER = "id";

	public SupprimerQuestions() {
		super(new QuestionModification(COMMENT_ID_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Comment comment = policy.getComment();
		model.questions.delete(comment.getId());
		RootingsUtils.rootToPage(IdeeQuestions.WEB_SERVLET + "?" + IdeeQuestions.IDEA_ID_PARAM + "=" + comment.getIdea(), req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}

}
