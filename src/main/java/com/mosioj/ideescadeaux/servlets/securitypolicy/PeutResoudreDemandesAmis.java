package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

public final class PeutResoudreDemandesAmis extends SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(PeutResoudreDemandesAmis.class);

	/**
	 * The user answer for each friendship request.
	 */
	private Map<Integer, Boolean> choiceParameters;

	private boolean hasAccess(HttpServletRequest request) throws SQLException {
		try {
			Map<String, String[]> params = request.getParameterMap();
			for (String key : params.keySet()) {
				if (!key.startsWith("choix_")) {
					continue;
				}
				if (params.get(key).length > 0) {
					choiceParameters.put(Integer.parseInt(key.substring("choix_".length())), "Accepter".equals(params.get(key)[0]));
				}
			}

			if (choiceParameters.isEmpty()) {
				lastReason = "Aucun utilisateur trouvé en paramètre.";
				return false;
			}

			int userId = connectedUser.id;
			for (int user : choiceParameters.keySet()) {
				if (user == userId) {
					lastReason = "Vous ne pouvez pas être ami avec vous-même...";
					return false;
				}
				if (model.userRelations.associationExists(userId, user)) {
					lastReason = "Vous êtes déjà ami avec l'une des personnes...";
					return false;
				}
				if (!model.userRelationRequests.associationExists(user, userId)) {
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
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return hasAccess(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			return hasAccess(request);
		} catch (SQLException e) {
			logger.error("Cannot process checking, SQLException: " + e);
			return false;
		}
	}

	/**
	 * @return the choiceParameters
	 */
	public Map<Integer, Boolean> getChoiceParameters() {
		return choiceParameters;
	}

	@Override
	public void reset() {
		choiceParameters = new HashMap<>();
	}

}
