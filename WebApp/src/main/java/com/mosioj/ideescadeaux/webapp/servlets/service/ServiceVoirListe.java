package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@WebServlet("/protected/service/voir_liste")
public class ServiceVoirListe extends ServiceGet<NetworkAccess> {

    /** The parameter name. */
    public static final String USER_ID_PARAM = "id";

    /** Class constructor. */
    public ServiceVoirListe() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Adding the list of ideas for each user
        final List<User> users = Collections.singletonList(policy.getUser());
        final List<OwnerIdeas> foundUsers = IdeaLogic.getPersonsIdeasFromUsers(thisOne, users, device);

        // Only one page : only fetching the list of one user...
        final List<Page> pages = Collections.singletonList(new Page(1));

        // Sending the response
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, foundUsers), isAdmin(request), thisOne));
    }
}
