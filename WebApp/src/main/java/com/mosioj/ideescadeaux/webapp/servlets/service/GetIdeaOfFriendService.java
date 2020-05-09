package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * Do not use for the users' idea.
 *
 * @author Jordan Mosio
 */
@WebServlet("/protected/service/get_idea_of_friend")
public class GetIdeaOfFriendService extends IdeesCadeauxGetServlet<IdeaInteraction> {

    private static final long serialVersionUID = -3425240682690763149L;
    public static final String IDEA_ID_PARAM = "idee";

    public GetIdeaOfFriendService() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        // Getting the idea
        Idee idee = policy.getIdea();

        // Decorated it
        DecoratedWebAppIdea decorated = new DecoratedWebAppIdea(idee, thisOne, device);

        // Writing answer
        buildResponse(response, ServiceResponse.ok(decorated, isAdmin(request), thisOne));
    }
}
