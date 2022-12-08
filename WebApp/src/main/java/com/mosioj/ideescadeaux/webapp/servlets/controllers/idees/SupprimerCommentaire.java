package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.text.Comment;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CommentModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/supprimer_commentaire")
public class SupprimerCommentaire extends IdeesCadeauxGetServlet<CommentModification> {

    private static final String COMMENT_ID_PARAMETER = "id";

    public SupprimerCommentaire() {
        super(new CommentModification(COMMENT_ID_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        Comment comment = policy.getComment();
        CommentsRepository.delete(comment.getId());
        RootingsUtils.rootToPage(IdeaComments.WEB_SERVLET + "?" + IdeaComments.IDEA_ID_PARAM + "=" + comment.getIdea(),
                                 req,
                                 resp);
    }

}
