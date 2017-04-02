package com.mosioj.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.NetworkGetAndAccessToPost;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	private static final Logger logger = LogManager.getLogger(AfficherReseau.class);

	private static final String USER_ID_PARAM = "id";
	private static final String GET_URL = "/protected/afficher_reseau?id=";
	private static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

	/**
	 * Class constructor.
	 */
	public AfficherReseau() {
		super(new NetworkGetAndAccessToPost(userRelations, USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer user = ParametersUtils.readInt(req, USER_ID_PARAM);

		int userId = ParametersUtils.getUserId(req);
		req.setAttribute("demandes", userRelationRequests.getRequests(userId));
		req.setAttribute("relations", userRelations.getRelations(user));
		req.setAttribute("name", users.getUser(user).name);

		RootingsUtils.rootToPage(DISPATCH_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(request);

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
			} else {
				logger.info(MessageFormat.format(	"Refus de la demande par {0} de l'utilisateur {1}.",
													userId,
													key.substring("choix_".length())));
				userRelationRequests.cancelRequest(fromUserId, userId);
			}
		}

		// Redirection à la page d'administration
		RootingsUtils.redirectToPage(GET_URL + userId, request, response);

	}

}
