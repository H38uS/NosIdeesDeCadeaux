package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNewCommentOnIdea;
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

    private static final long serialVersionUID = -433226623397937479L;
    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_commentaires.jsp";
    public static final String WEB_SERVLET = "/protected/idee_commentaires";

    public IdeaComments() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    /**
     * Drops all notification linked to questions of the given owner links to the given idea.
     *
     * @param owner  The notification owner.
     * @param ideaId The idea id.
     */
    private void dropNotificationOnView(User owner, int ideaId) {
        NotificationsRepository.removeAllType(owner,
                                              NotificationType.NEW_COMMENT_ON_IDEA,
                                              ParameterName.IDEA_ID,
                                              ideaId);
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        request.setAttribute("idee", idea);
        request.setAttribute("comments", CommentsRepository.getCommentsOn(idea.getId()));
        dropNotificationOnView(thisOne, idea.getId());
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        String text = ParametersUtils.readAndEscape(request, "text");

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

        for (User notified : toBeNotified) {
            NotificationsRepository.addNotification(notified.id, new NotifNewCommentOnIdea(current, idea));
        }

        dropNotificationOnView(thisOne, idea.getId());
        RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + idea.getId(), request, response);
    }

}
