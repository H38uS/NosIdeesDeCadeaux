package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository.getIdeaFromGroup;

@WebServlet("/protected/suggerer_groupe_idee")
public class SuggestGroupIdea extends IdeesCadeauxGetServlet<BookingGroupInteraction> {

    /** Service group identifier parameter. */
    public static final String GROUP_ID_PARAM = "groupid";

    /** The page to display. */
    public static final String VIEW_PAGE_URL = "/protected/suggest_group_idea.jsp";

    /** Class constructor. */
    public SuggestGroupIdea() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Parameters
        IdeaGroup group = policy.getGroupId();
        Idee idee = getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

        // JSP variables
        request.setAttribute("idee", idee);
        request.setAttribute("group", group);

        // The page
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
