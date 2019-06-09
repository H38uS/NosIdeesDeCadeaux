package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;

@WebServlet("/protected/service/demande_rejoindre_reseau")
public class DemandeRejoindreReseauService extends AbstractServicePost<PeutDemanderARejoindreLeReseau> {

	private static final long serialVersionUID = 3683476983071872342L;
	private static final Logger logger = LogManager.getLogger(DemandeRejoindreReseauService.class);

	public static final String USER_ID_PARAM = "user_id";

	public DemandeRejoindreReseauService() {
		super(new PeutDemanderARejoindreLeReseau(USER_ID_PARAM));
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

		String status = "ok";
		String message = "";

		try {
			User userToSendInvitation = policy.getUser();
			request.setAttribute("name", userToSendInvitation.getName());

			if (model.userRelationRequests.associationExists(thisOne.id, userToSendInvitation.id)) {
				throw new SQLException(MessageFormat.format("vous avez déjà envoyé une demande à {0}.",
															userToSendInvitation.getName()));
			}

			if (model.userRelations.associationExists(thisOne.id, userToSendInvitation.id)) {
				throw new SQLException(MessageFormat.format("vous êtes déjà ami avec {0}.", userToSendInvitation.getName()));
			}

			// Suppression des notifications
			model.notif.removeAllType(thisOne, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userToSendInvitation.id);
			model.notif.removeAllType(userToSendInvitation, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, thisOne);

			// On ajoute l'association
			model.userRelationRequests.insert(thisOne, userToSendInvitation);
			model.notif.addNotification(	userToSendInvitation.id,
									new NotifNouvelleDemandeAmi(thisOne, userToSendInvitation.id, thisOne.getName()));
		} catch (SQLException e) {
			status = "ko";
			message = e.getMessage();
			logger.warn(e);
		}

		writter.writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("error_message", message));
	}

}
