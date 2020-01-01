package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifFriendshipDropped;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.User;

@WebServlet("/protected/service/supprimer_relation")
public class ServiceSupprimerRelation extends IdeesCadeauxPostServlet<NetworkAccess> {

    private static final long serialVersionUID = -4896678945281607617L;
    private static final Logger logger = LogManager.getLogger(ServiceSupprimerRelation.class);
    public static final String USER_PARAMETER = "id";

    public ServiceSupprimerRelation() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

        ServiceResponse<String> ans;
        try {
            User user = policy.getUser();
            UserRelationsRepository.deleteAssociation(user.id, thisOne.id);
            NotificationsRepository.removeAllType(thisOne, NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, user.id);
            NotificationsRepository.removeAllType(UsersRepository.getUser(user.id),
                                                  NotificationType.ACCEPTED_FRIENDSHIP,
                                                  ParameterName.USER_ID,
                                                  thisOne);

            // Send a notification
            NotificationsRepository.addNotification(user.id, new NotifFriendshipDropped(thisOne));
            ans = ServiceResponse.ok(isAdmin(request));
        } catch (SQLException e) {
            ans = ServiceResponse.ko(e.getMessage(), isAdmin(request));
            logger.warn(e);
        }

        buildResponse(response, ans);
    }

}
