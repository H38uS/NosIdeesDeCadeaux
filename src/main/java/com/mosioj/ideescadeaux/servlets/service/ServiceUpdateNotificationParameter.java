package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

@WebServlet("/protected/service/update_notification_parameter")
public class ServiceUpdateNotificationParameter extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 8087174276226168482L;
    private static final Logger logger = LogManager.getLogger(ServiceUpdateNotificationParameter.class);

    public ServiceUpdateNotificationParameter() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        String name = ParametersUtils.readAndEscape(request, "name");
        String value = ParametersUtils.readAndEscape(request, "value");

        String message = "";
        try {
            NotificationType.valueOf(name);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            message = "Type de notification inconnu...";
        }
        try {
            NotificationActivation.valueOf(value);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            message = "Valeur inconnue...";
        }

        if (!message.isEmpty()) {
            buildResponse(response, ServiceResponse.ko(message, isAdmin(request)));
            return;
        }

        // Valid parameters, doing the update
        UserParametersRepository.insertUpdateParameter(thisOne, name, value);
        buildResponse(response, ServiceResponse.ok(isAdmin(request)));
    }
}
