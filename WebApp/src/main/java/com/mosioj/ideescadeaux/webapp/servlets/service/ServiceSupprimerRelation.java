package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/supprimer_relation")
public class ServiceSupprimerRelation extends ServicePost<NetworkAccess> {

    public static final String USER_PARAMETER = "id";

    public ServiceSupprimerRelation() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        User user = policy.getUser();
        UserRelationsRepository.deleteAssociation(user, thisOne);

        // Deletes old notifications
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.ACCEPTED_FRIENDSHIP)
                               .whereUser(user)
                               .terminates();
        NotificationsRepository.terminator()
                               .whereOwner(user)
                               .whereType(NType.ACCEPTED_FRIENDSHIP)
                               .whereUser(thisOne)
                               .terminates();

        // Send a notification
        NType.FRIENDSHIP_DROPPED.with(thisOne).sendItTo(user);

        buildResponse(response, ServiceResponse.ok(thisOne));
    }

}
