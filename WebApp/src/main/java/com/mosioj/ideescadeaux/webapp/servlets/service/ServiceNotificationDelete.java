package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NotificationModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@WebServlet("/protected/service/notification_delete")
public class ServiceNotificationDelete extends ServicePost<NotificationModification> {

    public static final String NOTIFICATION_PARAMETER = "notif_id";

    private static final Logger logger = LogManager.getLogger(ServiceNotificationDelete.class);

    public ServiceNotificationDelete() {
        super(new NotificationModification(NOTIFICATION_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        NotificationsRepository.remove(policy.getNotification());
        logger.info(MessageFormat.format("Suppression de la notification {0}", policy.getNotification()));
        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
