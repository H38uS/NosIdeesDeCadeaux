package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_ON_IDEA;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_TO_OWNER;

@WebServlet("/protected/idee_questions")
public class IdeeQuestions extends IdeesCadeauxGetAndPostServlet<CanAskReplyToQuestions> {

    private static final Logger logger = LogManager.getLogger(IdeeQuestions.class);
    public static final String IDEA_ID_PARAM = "idee";
    public static final String VIEW_PAGE_URL = "/protected/idee_questions.jsp";
    public static final String WEB_SERVLET = "/protected/idee_questions";

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
        request.setAttribute("comments", QuestionsRepository.getQuestionsOn(idea));
        dropNotificationOnView(thisOne, idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        logger.info(MessageFormat.format("Ajout d''une question sur l''idée {0}...", idea.getId()));
        String text = ParametersUtils.readAndEscape(request, "text");

        QuestionsRepository.addQuestion(thisOne, idea, text);

        Set<User> toBeNotified = new HashSet<>();

        // If the idea is booked, we notify the bookers
        toBeNotified.addAll(idea.getBookers());

        // Notifying at least all people in the thread
        toBeNotified.addAll(QuestionsRepository.getUserListOnQuestion(idea));

        // Faut que le owner soit au courant des questions :)
        toBeNotified.add(idea.owner);

        // Removing current user, and notifying others
        toBeNotified.remove(thisOne);
        logger.debug("Personnes à prévenir : {}.", toBeNotified);
        toBeNotified.stream()
                    .map(u -> idea.owner.equals(u) ?
                            NEW_QUESTION_TO_OWNER.with(thisOne, idea).setOwner(u) :
                            NEW_QUESTION_ON_IDEA.with(thisOne, idea).setOwner(u))
                    .forEach(Notification::send);

        dropNotificationOnView(thisOne, idea);
        RootingsUtils.redirectToPage(WEB_SERVLET + "?" + IDEA_ID_PARAM + "=" + idea.getId(), request, response);
    }

}
