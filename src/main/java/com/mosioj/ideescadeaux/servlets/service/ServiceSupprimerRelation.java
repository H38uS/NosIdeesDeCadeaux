package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifFriendshipDropped;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NetworkAccess;

@WebServlet("/protected/service/supprimer_relation")
public class ServiceSupprimerRelation extends AbstractServicePost<NetworkAccess> {

    private static final long serialVersionUID = -4896678945281607617L;
    private static final Logger logger = LogManager.getLogger(ServiceSupprimerRelation.class);
    public static final String USER_PARAMETER = "id";

    public ServiceSupprimerRelation() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

        ServiceResponse ans;
        try {
            User user = policy.getUser();
            model.userRelations.deleteAssociation(user.id, thisOne.id);
            model.notif.removeAllType(thisOne, NotificationType.ACCEPTED_FRIENDSHIP, ParameterName.USER_ID, user.id);
            model.notif.removeAllType(model.users.getUser(user.id),
                                      NotificationType.ACCEPTED_FRIENDSHIP,
                                      ParameterName.USER_ID,
                                      thisOne);

            // Send a notification
            model.notif.addNotification(user.id, new NotifFriendshipDropped(thisOne));
            ans = ServiceResponse.ok(isAdmin(request));
        } catch (SQLException e) {
            ans = ServiceResponse.ko(e.getMessage(), true, isAdmin(request));
            logger.warn(e);
        }

        buildResponse(response, ans);
    }

}
