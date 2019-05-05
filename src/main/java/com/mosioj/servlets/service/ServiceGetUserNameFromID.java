package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/get_user_name")
public class ServiceGetUserNameFromID extends AbstractService<NetworkAccess> {

	private static final long serialVersionUID = 8894577701063844430L;
	private static final Logger logger = LogManager.getLogger(ServiceGetUserNameFromID.class);

	private static final String USER_ID_PARAM = "userId";

	public ServiceGetUserNameFromID() {
		super(new NetworkAccess(USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		logger.debug(MessageFormat.format(	"Récupération du nom de l''utilisateur numéro {0}",
											ParametersUtils.readIt(request, USER_ID_PARAM)));
		Integer userId = ParametersUtils.readInt(request, USER_ID_PARAM).get();
		String status = "ko";
		String res;

		try {
			if (userId == null) {
				throw new SQLException("User id is null...");
			}
			User user = model.users.getUser(userId);
			res = user.getEmail();
			status = "ok";
		} catch (SQLException e) {
			res = MessageFormat.format("Erreur lors de la récupération du nom de l''utilisateur {0}...", userId);
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		writter.writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("message", res));
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Do nothing
	}
}
