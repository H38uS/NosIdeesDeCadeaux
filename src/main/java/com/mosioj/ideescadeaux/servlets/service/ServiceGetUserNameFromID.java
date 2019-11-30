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

@WebServlet("/protected/service/get_user_name")
public class ServiceGetUserNameFromID extends AbstractServiceGet<NetworkAccess> {

	private static final long serialVersionUID = 8894577701063844430L;
	private static final Logger logger = LogManager.getLogger(ServiceGetUserNameFromID.class);

	private static final String USER_ID_PARAM = "userId";

	public ServiceGetUserNameFromID() {
		super(new NetworkAccess(USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		User user = policy.getUser();
		logger.debug(MessageFormat.format("Récupération du nom de l''utilisateur numéro {0}", user.id));
		writter.writeJSonOutput(response, makeJSonPair("status", "ok"), makeJSonPair("message", user.getEmail()));
		// FIXME : 1 tester les services quand on a pas les droits
	}
}
