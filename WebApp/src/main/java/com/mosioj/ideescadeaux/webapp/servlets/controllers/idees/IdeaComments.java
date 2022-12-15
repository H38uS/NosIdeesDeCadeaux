package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/idee_commentaires")
public class IdeaComments extends IdeesCadeauxGetServlet<IdeaInteraction> {

    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";

    public IdeaComments() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("idee", policy.getIdea());
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
