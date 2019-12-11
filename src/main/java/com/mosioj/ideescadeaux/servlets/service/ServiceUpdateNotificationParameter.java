package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        boolean status = false;
        try {
            if (name != null && value != null) {
                NotificationType.valueOf(name);
                model.userParameters.insertUpdateParameter(thisOne, name, value);
                status = true;
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }

        buildResponse(response, new ServiceResponse(status, "", true, isAdmin(request)));
    }
}
