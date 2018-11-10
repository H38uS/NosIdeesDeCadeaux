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
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/resoudre_demande_ami")
public class ResoudreDemandeAmi extends IdeesCadeauxServlet<PeutResoudreDemandesAmis> {

	private static final long serialVersionUID = 454017088023043164L;
	private static final Logger logger = LogManager.getLogger(ResoudreDemandeAmi.class);

	/**
	 * Class constructor.
	 */
	public ResoudreDemandeAmi() {
		super(new PeutResoudreDemandesAmis(userRelations, userRelationRequests));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(	MessageFormat.format(	"{0}?{1}={2}",
																AfficherReseau.SELF_VIEW,
																AfficherReseau.USER_ID_PARAM,
																ParametersUtils.readInt(request, AfficherReseau.USER_ID_PARAM)),
										request,
										response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		User user = users.getUser(userId);

		List<User> accepted = new ArrayList<User>();
		Map<String, String[]> params = request.getParameterMap();
		Set<AbstractNotification> toBeRemoved = new HashSet<AbstractNotification>();
		for (String key : params.keySet()) {

			if (!key.startsWith("choix_")) {
				continue;
			}

			int fromUserId = Integer.parseInt(key.substring("choix_".length()));
			if (!userRelationRequests.associationExists(fromUserId, userId)) {
				// On ne traite que les demandes réellement envoyées...
				continue;
			}

			boolean accept = "Accepter".equals(params.get(key)[0]);

			if (accept) {
				logger.info(MessageFormat.format("Approbation de la demande par {0} de l'utilisateur {1}.", userId, fromUserId));
				userRelations.addAssociation(fromUserId, userId);
				userRelationRequests.cancelRequest(fromUserId, userId);
				accepted.add(users.getUser(fromUserId));
				notif.addNotification(fromUserId, new NotifDemandeAcceptee(user.id, user.getName()));
			} else {
				logger.info(MessageFormat.format("Refus de la demande par {0} de l'utilisateur {1}.", userId, fromUserId));
				userRelationRequests.cancelRequest(fromUserId, userId);
				notif.addNotification(fromUserId, new NotifDemandeRefusee(user.id, user.getName()));
			}

			// Si fromUserId avait supprimé sa relation avec userId
			toBeRemoved.addAll(notif.getNotifications(userId, NotificationType.FRIENDSHIP_DROPPED, ParameterName.USER_ID, fromUserId));
			// Si userId avait supprimé sa relation avec fromUserId
			toBeRemoved.addAll(notif.getNotifications(fromUserId, NotificationType.FRIENDSHIP_DROPPED, ParameterName.USER_ID, userId));
			// Si fromUserId avait refusé la demande de userId
			toBeRemoved.addAll(notif.getNotifications(userId, NotificationType.REJECTED_FRIENDSHIP, ParameterName.USER_ID, fromUserId));
			// Si userId avait supprimé sa relation avec fromUserId
			toBeRemoved.addAll(notif.getNotifications(fromUserId, NotificationType.REJECTED_FRIENDSHIP, ParameterName.USER_ID, userId));
			
			// Suppression des suggestions d'amitiés entre ces deux personnes
			toBeRemoved.addAll(notif.getNotifications(userId, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, fromUserId));
			toBeRemoved.addAll(notif.getNotifications(fromUserId, NotificationType.NEW_RELATION_SUGGESTION, ParameterName.USER_ID, userId));
			
			// Suppression des demandes d'amis
			toBeRemoved.addAll(notif.getNotifications(fromUserId, NotificationType.NEW_FRIENSHIP_REQUEST, ParameterName.USER_ID, userId));
		}
		
		for (AbstractNotification n : toBeRemoved) {
			notif.remove(n.id);
		}
		
		notif.removeAllType(userId, NotificationType.NEW_FRIENSHIP_REQUEST);

		// Redirection à la page d'administration
		HttpSession session = request.getSession();
		session.setAttribute("accepted", accepted);
		RootingsUtils.redirectToPage(	MessageFormat.format(	"{0}?{1}={2}",
																AfficherReseau.SELF_VIEW,
																AfficherReseau.USER_ID_PARAM,
																userId),
										request,
										response);
	}

}
