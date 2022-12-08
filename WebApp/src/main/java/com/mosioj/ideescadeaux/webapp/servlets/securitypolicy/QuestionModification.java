package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.text.Question;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.QuestionSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuestionModification extends SecurityPolicy implements QuestionSecurityChecker {

    /**
     * Defines the string used in HttpServletRequest to retrieve the comment id.
     */
    private final String questionParameter;

    private Question question;

    /**
     * @param questionParameter Defines the string used in HttpServletRequest to retrieve the comment id.
     */
    public QuestionModification(String questionParameter) {
        this.questionParameter = questionParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canModifyQuestion(HttpServletRequest request) {

        question = ParametersUtils.readInt(request, questionParameter)
                                  .flatMap(QuestionsRepository::getQuestion)
                                  .orElse(null);
        if (question == null) {
            lastReason = "Aucun commentaire trouvé en paramètre.";
            return false;
        }

        boolean res = connectedUser.equals(question.getWrittenBy());
        if (!res) {
            lastReason = "Vous ne pouvez modifier que vos commentaires.";
        }

        return res;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return canModifyQuestion(request);
    }

    @Override
    public boolean hasRightToInteractInPostRequest(final HttpServletRequest request,
                                                   final HttpServletResponse response) {
        return canModifyQuestion(request);
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public void reset() {
        question = null;
    }
}
