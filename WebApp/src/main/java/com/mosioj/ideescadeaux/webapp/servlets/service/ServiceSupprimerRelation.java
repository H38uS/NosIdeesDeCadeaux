package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifFriendshipDropped;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/supprimer_relation")
public class ServiceSupprimerRelation extends IdeesCadeauxPostServlet<NetworkAccess> {

    private static final long serialVersionUID = -4896678945281607617L;
    public static final String USER_PARAMETER = "id";

    public ServiceSupprimerRelation() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        User user = policy.getUser();
        UserRelationsRepository.deleteAssociation(user.id, thisOne.id);
        NotificationsRepository.removeAllType(thisOne,
                                              NotificationType.ACCEPTED_FRIENDSHIP,
                                              ParameterName.USER_ID,
                                              user.id);
        UsersRepository.getUser(user.id).ifPresent(u -> NotificationsRepository.removeAllType(u,
                                                                                              NotificationType.ACCEPTED_FRIENDSHIP,
                                                                                              ParameterName.USER_ID,
                                                                                              thisOne));

        // Send a notification
        NotificationsRepository.addNotification(user.id, new NotifFriendshipDropped(thisOne));

        buildResponse(response, ServiceResponse.ok(isAdmin(request)));
    }

}
