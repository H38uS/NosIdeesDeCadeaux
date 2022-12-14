package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.entities.text.Question;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CanAskReplyToQuestions;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_ON_IDEA;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.NEW_QUESTION_TO_OWNER;

@WebServlet("/protected/service/idea_questions")
public class ServiceQuestions extends ServiceGetAndPost<CanAskReplyToQuestions> {

    /** The idea on which we want to add a question/answer */
    public static final String IDEA_ID_PARAM = "idea";

    /**
     * Class constructor.
     */
    public ServiceQuestions() {
        super(new CanAskReplyToQuestions(IDEA_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {
        final Idee idea = policy.getIdea();
        List<QuestionWithFlags> questions = QuestionsRepository.getQuestionsOn(idea)
                                                               .stream()
                                                               .map(q -> new QuestionWithFlags(q,
                                                                                               thisOne,
                                                                                               idea.getOwner()))
                                                               .toList();
        buildResponse(response, ServiceResponse.ok(questions, thisOne));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        Idee idea = policy.getIdea();
        QuestionsRepository.addQuestion(thisOne, idea, ParametersUtils.getPOSTParameterAsString(request, "text"));

        getUsersToBeNotified(idea).stream()
                                  .map(u -> idea.owner.equals(u) ?
                                          NEW_QUESTION_TO_OWNER.with(thisOne, idea).setOwner(u) :
                                          NEW_QUESTION_ON_IDEA.with(thisOne, idea).setOwner(u))
                                  .forEach(Notification::send);

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
        toBeNotified.addAll(QuestionsRepository.getUserListOnQuestion(idea));

        // Faut que le owner soit au courant des questions :)
        toBeNotified.add(idea.owner);

        // Removing current user, and notifying others
        toBeNotified.remove(thisOne);
        return toBeNotified;
    }

    private static class QuestionWithFlags extends Question {

        /** True if this message was written by me */
        @Expose
        private final boolean isMyMessage;

        /** True if this message was written by the idea owner */
        @Expose
        private final boolean isFromIdeaOwner;

        /** When this message was last edited */
        @Expose
        private final String lastEditedOn;

        /** The idea owner */
        @Expose
        private final User ideaOwner;

        private QuestionWithFlags(Question question, User connectedOne, User ideaOwner) {
            this.id = question.getId();
            this.htmlText = question.getHtml();
            this.isMyMessage = connectedOne.equals(question.getWrittenBy());
            this.isFromIdeaOwner = ideaOwner.equals(question.getWrittenBy());
            this.lastEditedOn = MyDateFormatViewer.formatMine(question.getUpdatedAt()
                                                                      .or(question::getCreationDate)
                                                                      .orElse(null));
            this.ideaOwner = ideaOwner;
        }
    }
}
