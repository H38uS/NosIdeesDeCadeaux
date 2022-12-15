package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CommentModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/delete_comment")
public class ServiceSupprimerComments extends ServicePost<CommentModification> {

    /** The parameter holding the message identifier */
    protected static final String MESSAGE_ID_PARAMETER = "id";

    /**
     * Class constructor.
     */
    public ServiceSupprimerComments() {
        super(new CommentModification(MESSAGE_ID_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        CommentsRepository.delete(policy.getComment());
        buildResponse(response, ServiceResponse.ok(thisOne));
    }

}
