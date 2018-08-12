package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.servlets.controllers.relations.AfficherReseau;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class PeutResoudreDemandesAmis extends AllAccessToPostAndGet implements SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(AfficherReseau.class);

	private final UserRelations userRelations;
	private final UserRelationRequests userRelationRequests;

	/**
	 * 
	 * @param userRelations
	 * @param userRelationRequests
	 */
	public PeutResoudreDemandesAmis(UserRelations userRelations, UserRelationRequests userRelationRequests) {
		this.userRelations = userRelations;
		this.userRelationRequests = userRelationRequests;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {
		try {
			Set<Integer> ids = new HashSet<Integer>();
			Map<String, String[]> params = request.getParameterMap();
			for (String key : params.keySet()) {
				if (!key.startsWith("choix_")) {
					continue;
				}
				ids.add(Integer.parseInt(key.substring("choix_".length())));
			}

			if (ids.isEmpty()) {
				lastReason = "Aucun utilisateur trouvé en paramètre.";
				return false;
			}

			int userId = ParametersUtils.getUserId(request);
			for (int user : ids.toArray(new Integer[ids.size()])) {
				if (user == userId) {
					lastReason = "Vous ne pouvez pas être ami avec vous-même...";
					return false;
				}
				if (userRelations.associationExists(userId, user)) {
					lastReason = "Vous êtes déjà ami avec l'une des personnes...";
					return false;
				}
				if (!userRelationRequests.associationExists(user, userId)) {
					lastReason = "Au moins une personne ne vous a jamais fait de demande...";
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			logger.error("Unable to process the security check: " + e.getMessage());
			lastReason = "Une erreur est survenue lors de la vérification des droits. Veuillez réessayer, ou contacter l'administrateur.";
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInPostRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

	@Override
	public boolean hasRightToInteractInGetRequest(	HttpServletRequest request,
													HttpServletResponse response) throws SQLException, NotLoggedInException {
		return hasAccess(request);
	}

}
