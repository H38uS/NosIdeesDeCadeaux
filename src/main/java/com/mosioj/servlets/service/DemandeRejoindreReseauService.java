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
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/demande_rejoindre_reseau")
public class DemandeRejoindreReseauService extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 3683476983071872342L;
	private static final Logger logger = LogManager.getLogger(DemandeRejoindreReseauService.class);

	public static final String USER_ID_PARAM = "user_id";

	public DemandeRejoindreReseauService() {
		super(new PeutDemanderARejoindreLeReseau(userRelations, userRelationRequests, USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// RAS
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

		String status = "ok";
		String message = "";

		try {
			User userToSendInvitation = users.getUser(ParametersUtils.readInt(request, USER_ID_PARAM));
			int userId = ParametersUtils.getUserId(request);
			request.setAttribute("name", userToSendInvitation.getName());

			if (userRelationRequests.associationExists(userId, userToSendInvitation.id)) {
				throw new SQLException(MessageFormat.format("vous avez déjà envoyé une demande à {0}.",
															userToSendInvitation.getName()));
			}

			if (userRelations.associationExists(userId, userToSendInvitation.id)) {
				throw new SQLException(MessageFormat.format("vous êtes déjà ami avec {0}.", userToSendInvitation.getName()));
			}

			// Suppression des notifications
			notif.removeAllType(userId, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userToSendInvitation.id);
			notif.removeAllType(userToSendInvitation.id, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userId);

			// On ajoute l'association
			userRelationRequests.insert(userId, userToSendInvitation.id);
			notif.addNotification(	userToSendInvitation.id,
									new NotifNouvelleDemandeAmi(userId, userToSendInvitation.id, users.getUser(userId).getName()));
		} catch (SQLException | NotLoggedInException e) {
			status = "ko";
			message = e.getMessage();
			logger.warn(e);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("error_message", message));
	}

}
