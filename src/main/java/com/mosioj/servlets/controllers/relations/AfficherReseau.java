package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Relation;
import com.mosioj.model.User;
import com.mosioj.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.notifications.instance.NotifDemandeRefusee;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	private static final Logger logger = LogManager.getLogger(AfficherReseau.class);

	private static final String USER_ID_PARAM = "id";
	public static final String URL = "/protected/afficher_reseau";
	private static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

	/**
	 * Class constructor.
	 */
	public AfficherReseau() {
		super(new NetworkAccess(userRelations, USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer user = ParametersUtils.readInt(req, USER_ID_PARAM);

		int userId = ParametersUtils.getUserId(req);

		if (userId == user) {
			// Uniquement sur notre compte
			req.setAttribute("demandes", userRelationRequests.getRequests(userId));
			req.setAttribute("suggestions", userRelationsSuggestion.hasReceivedSuggestion(userId));
		}
		
		// Ajout du flag network
		List<Relation> relations = userRelations.getRelations(user);
		List<Relation> mine = userRelations.getRelations(userId);
		for (Relation r : relations) {
			if (mine.contains(r)) {
				r.secondIsInMyNetwork = true;
			}
		}

		req.setAttribute("id", user);
		req.setAttribute("relations", relations);
		req.setAttribute("name", users.getUser(user).name);

		RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);
		User user = users.getUser(userId);

		List<User> accepted = new ArrayList<User>();
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {

			if (!key.startsWith("choix")) {
				continue;
			}

			int fromUserId = Integer.parseInt(key.substring("choix_".length()));
			if (!userRelationRequests.associationExists(fromUserId, userId)) {
				// On ne traite que les demandes réellement envoyées...
				continue;
			}

			boolean accept = "Accepter".equals(params.get(key)[0]);

			if (accept) {
				logger.info(MessageFormat.format(	"Approbation de la demande par {0} de l'utilisateur {1}.",
													userId,
													key.substring("choix_".length())));
				userRelations.addAssociation(fromUserId, userId);
				userRelationRequests.cancelRequest(fromUserId, userId);
				accepted.add(users.getUser(fromUserId));
				notif.addNotification(fromUserId, new NotifDemandeAcceptee(user.id, user.name));
			} else {
				logger.info(MessageFormat.format(	"Refus de la demande par {0} de l'utilisateur {1}.",
													userId,
													key.substring("choix_".length())));
				userRelationRequests.cancelRequest(fromUserId, userId);
				notif.addNotification(fromUserId, new NotifDemandeRefusee(user.id, user.name));
			}
		}

		// Redirection à la page d'administration
		request.setAttribute("accepted", accepted);
		ideesKDoGET(request, response);
	}

}
