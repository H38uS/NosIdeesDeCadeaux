package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;

@WebServlet("/protected/service/get_user_name")
public class ServiceGetUserNameFromID extends com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet<NetworkAccess> {

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
        // FIXME : 8 tester les services quand on a pas les droits
    }
}
