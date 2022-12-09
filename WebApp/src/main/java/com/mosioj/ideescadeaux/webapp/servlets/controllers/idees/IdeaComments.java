package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/protected/idee_commentaires")
public class IdeaComments extends IdeesCadeauxGetAndPostServlet<IdeaInteraction> {

    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";
    public static final String WEB_SERVLET = "/protected/idee_commentaires";

    public IdeaComments() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    /**
     * Drops all notification linked to questions of the given owner links to the given idea.
     *
     * @param owner The notification owner.
     * @param idea  The idea.
     */
    private void dropNotificationOnView(User owner, Idee idea) {
        NotificationsRepository.terminator()
                               .whereOwner(owner)
                               .whereType(NType.NEW_COMMENT_ON_IDEA)
                               .whereIdea(idea)
                               .terminates();
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        request.setAttribute("idee", idea);
        request.setAttribute("comments", CommentsRepository.getCommentsOn(idea.getId()));
        dropNotificationOnView(thisOne, idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        // TODO faire un service
        String text = ParametersUtils.readAndEscape(request, "text", true);

        User current = thisOne;
        Idee idea = policy.getIdea();
        CommentsRepository.addComment(current.id, idea.getId(), text);

        Set<User> toBeNotified = new HashSet<>();

        // If the idea is booked, we notify the bookers
        toBeNotified.addAll(idea.getBookers());

        // Notifying at least all people in the thread
        toBeNotified.addAll(CommentsRepository.getUserListOnComment(idea.getId()));

        // Removing current user, and notifying others
        toBeNotified.remove(current);

        Notification newComment = NType.NEW_COMMENT_ON_IDEA.with(current, idea);
        toBeNotified.forEach(newComment::sendItTo);

        dropNotificationOnView(thisOne, idea);
        RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + idea.getId(), request, response);
    }

}
