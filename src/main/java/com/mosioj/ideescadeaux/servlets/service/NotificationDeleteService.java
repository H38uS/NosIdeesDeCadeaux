package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.servlets.securitypolicy.NotificationModification;

@WebServlet("/protected/service/notification_delete")
public class NotificationDeleteService extends IdeesCadeauxPostServlet<NotificationModification> {

    private static final long serialVersionUID = 2642366164643542379L;
    private static final String NOTIFICATION_PARAMETER = "notif_id";

    private static final Logger logger = LogManager.getLogger(NotificationDeleteService.class);

    public NotificationDeleteService() {
        super(new NotificationModification(NOTIFICATION_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        model.notif.remove(policy.getNotificationId());
        logger.info(MessageFormat.format("Suppression de la notification {0}", policy.getNotificationId()));
        buildResponse(response, ServiceResponse.ok(isAdmin(request)));
    }
}
