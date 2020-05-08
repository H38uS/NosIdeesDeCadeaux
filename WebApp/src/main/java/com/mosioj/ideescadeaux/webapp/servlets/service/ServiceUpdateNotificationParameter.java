package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

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
            buildResponse(response, ServiceResponse.ko(message, isAdmin(request), thisOne));
            return;
        }

        // Valid parameters, doing the update
        UserParametersRepository.insertUpdateParameter(thisOne, name, value);
        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }
}
