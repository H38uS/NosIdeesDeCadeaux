package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/demande_rejoindre_reseau")
public class DemandeRejoindreReseau extends IdeesCadeauxServlet<PeutDemanderARejoindreLeReseau> {

	public static final String USER_ID_PARAM = "user_id";

	private static final long serialVersionUID = -7941136326499438776L;

	public static final String SUCCESS_URL = "/protected/demande_rejoindre_reseau_succes.jsp";
	public static final String ERROR_URL = "/protected/demande_rejoindre_reseau_error.jsp";

	/**
	 * Class constructor.
	 */
	public DemandeRejoindreReseau() {
		super(new PeutDemanderARejoindreLeReseau(userRelations, userRelationRequests, USER_ID_PARAM));
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User userToSendInvitation = users.getUser(ParametersUtils.readInt(request, USER_ID_PARAM));
		int userId = ParametersUtils.getUserId(request);
		request.setAttribute("name", userToSendInvitation.getName());

		// Suppression des notifications
		notif.removeAllType(userId, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userToSendInvitation.id);
		notif.removeAllType(userToSendInvitation.id, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userId);

		// On ajoute l'association
		userRelationRequests.insert(userId, userToSendInvitation.id);
		notif.addNotification(	userToSendInvitation.id,
								new NotifNouvelleDemandeAmi(userId, userToSendInvitation.id, users.getUser(userId).getName()));
		RootingsUtils.rootToPage(SUCCESS_URL, request, response);

	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		req.setAttribute("error_message", "Aucun utilisateur spécifié !");
		RootingsUtils.rootToPage(ERROR_URL, req, resp);
	}

}
