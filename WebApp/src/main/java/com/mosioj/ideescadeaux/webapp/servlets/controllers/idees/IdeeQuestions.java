package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_ON_IDEA;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_TO_OWNER;

@WebServlet("/protected/idee_questions")
public class IdeeQuestions extends IdeesCadeauxGetServlet<CanAskReplyToQuestions> {

    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_questions.jsp";

    public IdeeQuestions() {
        super(new CanAskReplyToQuestions(IDEA_ID_PARAM));
    }

    /**
     * Drops all notification linked to questions of the given owner links to the given idea.
     *
     * @param owner The owner.
     * @param idea  The idea parameter.
     */
    private void dropNotificationOnView(User owner, Idee idea) {
        NotificationsRepository.terminator()
                               .whereOwner(owner)
                               .whereType(NType.IDEA_ADDED_BY_FRIEND, NEW_QUESTION_ON_IDEA, NEW_QUESTION_TO_OWNER)
                               .whereIdea(idea)
                               .terminates();
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        request.setAttribute("idee", idea);
        request.setAttribute("isOwner", idea.owner == thisOne);
        dropNotificationOnView(thisOne, idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
