package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/update_notification_parameter")
public class ServiceUpdateNotificationParameter extends ServicePost<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(ServiceUpdateNotificationParameter.class);

    public ServiceUpdateNotificationParameter() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        String name = ParametersUtils.getPOSTParameterAsString(request, "name");
        String value = ParametersUtils.getPOSTParameterAsString(request, "value");

        String message = "";
        try {
            NType.valueOf(name);
        } catch (IllegalArgumentException e) {
            logger.error("Une erreur est survenue...", e);
            message = "Type de notification inconnu...";
        }
        try {
            NotificationActivation.valueOf(value);
        } catch (IllegalArgumentException e) {
            logger.error("Une erreur est survenue...", e);
            message = "Valeur inconnue...";
        }

        if (!message.isEmpty()) {
            buildResponse(response, ServiceResponse.ko(message, thisOne));
            return;
        }

        // Valid parameters, doing the update
        UserParametersRepository.insertUpdateParameter(thisOne, name, value);
        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
