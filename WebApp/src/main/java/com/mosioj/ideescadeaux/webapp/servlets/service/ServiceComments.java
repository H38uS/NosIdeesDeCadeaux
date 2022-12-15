package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/protected/service/idea_comments")
public class ServiceComments extends ServiceGetAndPost<IdeaInteraction> {

    /** The idea on which we want to add a message */
    public static final String IDEA_ID_PARAM = "idea";

    /**
     * Class constructor.
     */
    public ServiceComments() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Get the comments
        final Idee idea = policy.getIdea();
        var comments = CommentsRepository.getCommentsOn(idea);

        // Suppression des notifications
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_COMMENT_ON_IDEA)
                               .whereIdea(idea)
                               .terminates();

        // Sending the response
        buildResponse(response, ServiceResponse.ok(comments, thisOne));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        // Saving the comment
        Idee idea = policy.getIdea();
        CommentsRepository.addComment(thisOne, idea, ParametersUtils.getPOSTParameterAsString(request, "text"));

        // Sending notifications
        final Notification newComment = NType.NEW_COMMENT_ON_IDEA.with(thisOne, idea);
        getUsersToBeNotified(idea).forEach(newComment::sendItTo);

        // Sending the response
        buildResponse(response, ServiceResponse.ok(thisOne));
    }

    /**
     * @param idea The idea on which the question/answer is written.
     * @return All the users to be notified on this answer.
     */
    private Set<User> getUsersToBeNotified(Idee idea) {
        Set<User> toBeNotified = new HashSet<>();

        // If the idea is booked, we notify the bookers
        toBeNotified.addAll(idea.getBookers());

        // Notifying at least all people in the thread
        toBeNotified.addAll(CommentsRepository.getUserListOnComment(idea));

        // Removing current user
        toBeNotified.remove(thisOne);
        return toBeNotified;
    }

}
