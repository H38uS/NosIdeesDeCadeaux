package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.QuestionModification;
import com.mosioj.ideescadeaux.core.model.entities.Comment;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

@WebServlet("/protected/supprimer_question")
public class SupprimerQuestions extends IdeesCadeauxGetServlet<QuestionModification> {

    private static final long serialVersionUID = 7722016569684838786L;
    private static final String COMMENT_ID_PARAMETER = "id";

    public SupprimerQuestions() {
        super(new QuestionModification(COMMENT_ID_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        Comment comment = policy.getComment();
        QuestionsRepository.delete(comment.getId());
        RootingsUtils.rootToPage(IdeeQuestions.WEB_SERVLET +
                                 "?" +
                                 IdeeQuestions.IDEA_ID_PARAM +
                                 "=" +
                                 comment.getIdea(), req, resp);
    }

}
