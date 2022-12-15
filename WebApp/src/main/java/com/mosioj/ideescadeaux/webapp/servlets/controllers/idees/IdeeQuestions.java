package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/idee_questions")
public class IdeeQuestions extends IdeesCadeauxGetServlet<CanAskReplyToQuestions> {

    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_questions.jsp";

    public IdeeQuestions() {
        super(new CanAskReplyToQuestions(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        request.setAttribute("idee", idea);
        request.setAttribute("isOwner", idea.owner == thisOne);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
