package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.User;

@WebServlet("/protected/service/get_user_name")
public class ServiceGetUserNameFromID extends IdeesCadeauxGetServlet<NetworkAccess> {

    private static final long serialVersionUID = 8894577701063844430L;
    private static final Logger logger = LogManager.getLogger(ServiceGetUserNameFromID.class);

    public static final String USER_ID_PARAM = "userId";

    public ServiceGetUserNameFromID() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        User user = policy.getUser();
        logger.debug(MessageFormat.format("Récupération du nom de l''utilisateur {0}", user));
        buildResponse(response, ServiceResponse.ok(user, isAdmin(request)));
    }
}