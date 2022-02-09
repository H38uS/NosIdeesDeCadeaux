package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@WebServlet("/protected/service/get_user_name")
public class ServiceGetUserNameFromID extends ServiceGet<NetworkAccess> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceGetUserNameFromID.class);

    public static final String USER_ID_PARAM = "userId";

    public ServiceGetUserNameFromID() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {
        User user = policy.getUser();
        logger.debug(MessageFormat.format("Récupération du nom de l''utilisateur {0}", user));
        buildResponse(response, ServiceResponse.ok(user, thisOne));
    }
}
