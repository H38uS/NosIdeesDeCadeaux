package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.notifications.NotificationType;
import com.mosioj.notifications.ParameterName;
import com.mosioj.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.notifications.instance.NotifDemandeRefusee;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.PeutResoudreDemandesAmis;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/resoudre_demande_ami")
public class ResoudreDemandeAmi extends IdeesCadeauxServlet<PeutResoudreDemandesAmis> {

	private static final long serialVersionUID = 454017088023043164L;
	private static final Logger logger = LogManager.getLogger(ResoudreDemandeAmi.class);

	/**
	 * Class constructor.
	 */
	public ResoudreDemandeAmi() {
		super(new PeutResoudreDemandesAmis());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// Impossible - rien à faire
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int thisUserId = thisOne.id;

		List<User> accepted = new ArrayList<User>();
		Map<String, String[]> params = request.getParameterMap();
		Set<AbstractNotification> toBeRemoved = new HashSet<AbstractNotification>();
		for (String key : params.keySet()) { // FIXME : 1 déjà fait dans la police, faire un objet pour le passer

			if (!key.startsWith("choix_")) {
				continue;
			}

			int fromUserId = Integer.parseInt(key.substring("choix_".length()));
			if (!model.userRelationRequests.associationExists(fromUserId, thisUserId)) {
				// On ne traite que les demandes réellement envoyées...
				continue;
			}

			boolean accept = "Accepter".equals(params.get(key)[0]);

			if (accept) {
				logger.info(MessageFormat.format("Approbation de la demande par {0} de l'utilisateur {1}.", thisUserId, fromUserId));
				model.userRelations.addAssociation(fromUserId, thisUserId);
				model.userRelationRequests.cancelRequest(fromUserId, thisUserId);
				accepted.add(model.users.getUser(fromUserId));
				model.notif.addNotification(fromUserId, new NotifDemandeAcceptee(thisOne.id, thisOne.getName()));
			} else {
				logger.info(MessageFormat.format("Refus de la demande par {0} de l'utilisateur {1}.", thisUserId, fromUserId));
				model.userRelationRequests.cancelRequest(fromUserId, thisUserId);
				model.notif.addNotification(fromUserId, new NotifDemandeRefusee(thisOne.id, thisOne.getName()));
			}

			// Si fromUserId avait supprimé sa relation avec userId
			toBeRemoved.addAll(model.notif.getNotifications(thisUserId, NotificationType.FRIENDSHIP_DROPPED, ParameterName.USER_ID, fromUserId));
			// Si userId avait supprimé sa relation avec fromUserId
			toBeRemoved.addAll(model.notif.getNotifications(fromUserId, NotificationType.FRIENDSHIP_DROPPED, ParameterName.USER_ID, thisUserId));
			// Si fromUserId avait refusé la demande de userId
			toBeRemoved.addAll(model.notif.getNotifications(	thisUserId,
														NotificationType.REJECTED_FRIENDSHIP,
														ParameterName.USER_ID,
														fromUserId));
			// Si userId avait supprimé sa relation avec fromUserId
			toBeRemoved.addAll(model.notif.getNotifications(	fromUserId,
														NotificationType.REJECTED_FRIENDSHIP,
														ParameterName.USER_ID,
														thisUserId));

			// Suppression des suggestions d'amitiés entre ces deux personnes
			toBeRemoved.addAll(model.notif.getNotifications(	thisUserId,
														NotificationType.NEW_RELATION_SUGGESTION,
														ParameterName.USER_ID,
														fromUserId));
			toBeRemoved.addAll(model.notif.getNotifications(	fromUserId,
														NotificationType.NEW_RELATION_SUGGESTION,
														ParameterName.USER_ID,
														thisUserId));

			// Suppression des demandes d'amis
			toBeRemoved.addAll(model.notif.getNotifications(	fromUserId,
														NotificationType.NEW_FRIENSHIP_REQUEST,
														ParameterName.USER_ID,
														thisUserId));
		}

		for (AbstractNotification n : toBeRemoved) {
			model.notif.remove(n.id);
		}

		model.notif.removeAllType(thisOne, NotificationType.NEW_FRIENSHIP_REQUEST);

		// Redirection à la page d'administration
		HttpSession session = request.getSession();
		session.setAttribute("accepted", accepted);
		RootingsUtils.redirectToPage(AfficherReseau.SELF_VIEW + "?" + AfficherReseau.USER_ID_PARAM + "=" + thisUserId, request, response);
	}

}
