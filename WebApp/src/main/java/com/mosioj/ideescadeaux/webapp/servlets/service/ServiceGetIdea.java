package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.CanGetFriendsOrOwnNotSurpriseIdea;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Do not use for the users' idea.
 *
 * @author Jordan Mosio
 */
@WebServlet("/protected/service/get_idea")
public class ServiceGetIdea extends ServiceGet<CanGetFriendsOrOwnNotSurpriseIdea> {

    public static final String IDEA_ID_PARAM = "idee";

    public ServiceGetIdea() {
        // OK pour voir les idées des amis (surprise ou pas)
        // ou les siennes (uniquement quand ce ne sont pas des surprises)
        super(new CanGetFriendsOrOwnNotSurpriseIdea(IDEA_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Getting the idea
        Idee idee = policy.getIdea();

        // Decorated it
        DecoratedWebAppIdea decorated = new DecoratedWebAppIdea(idee, thisOne, device);

        // Writing answer
        buildResponse(response, ServiceResponse.ok(decorated, thisOne));
    }
}
