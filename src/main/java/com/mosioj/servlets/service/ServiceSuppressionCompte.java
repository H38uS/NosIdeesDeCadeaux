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
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/administration/service/supprimer_compte")
public class ServiceSuppressionCompte extends AbstractService {

	private static final long serialVersionUID = -8612163046284587669L;
	private static final Logger logger = LogManager.getLogger(ServiceSuppressionCompte.class);

	public static final String USER_ID_PARAM = "userId";

	/**
	 * Seuls les admins peuvent le faire
	 */
	public ServiceSuppressionCompte() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Rien à faire
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		if (!request.isUserInRole("ROLE_ADMIN")) {
			return;
		}

		Integer userId = ParametersUtils.readInt(request, USER_ID_PARAM);
		String status = "ko";
		String messageErreur = "";


		if (userId != null) {
			try {
				User user = users.getUser(userId);
				logger.info(MessageFormat.format("Suppression du compte {0} (id: {1})", user, userId));
				users.deleteUser(user);
				status = "ok";
			} catch (Exception e) {
				messageErreur = e.getMessage();
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			messageErreur = "L'utilisateur n'existe pas";
		}

		writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("error_message", messageErreur));
	}

}
