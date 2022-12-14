package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.QuestionModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/delete_question")
public class ServiceSupprimerQuestions extends ServicePost<QuestionModification> {

    /** The parameter holding the message identifier */
    protected static final String MESSAGE_ID_PARAMETER = "id";

    /**
     * Class constructor.
     */
    public ServiceSupprimerQuestions() {
        super(new QuestionModification(MESSAGE_ID_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        QuestionsRepository.delete(policy.getQuestion());
        buildResponse(response, ServiceResponse.ok(thisOne));
    }

}
