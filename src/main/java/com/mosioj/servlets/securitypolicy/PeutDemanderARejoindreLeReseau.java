package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class PeutDemanderARejoindreLeReseau extends AllAccessToPostAndGet {

	private static final Logger logger = LogManager.getLogger(PeutDemanderARejoindreLeReseau.class);

	private final String userParameter;

	/**
	 */
	public PeutDemanderARejoindreLeReseau(	String userParameter) {
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {

		try {
			int userId = connectedUser.id;

			// Y a-t-il un utilisateur ?
			Optional<Integer> toBeSentTo = ParametersUtils.readInt(request, userParameter);
			if (!toBeSentTo.isPresent()) {
				lastReason = "Aucun utilisateur trouvé en paramètre.";
				return false;
			}

			if (toBeSentTo.get() == userId || model.userRelations.associationExists(toBeSentTo.get(), userId)) {
				lastReason = "Vous faites déjà parti du même réseau.";
				return false;
			}

			if (model.userRelationRequests.associationExists(userId, toBeSentTo.get())) {
				lastReason = "Vous avez déjà envoyé une demande pour cette personne.";
				return false;
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
		lastReason = "L'accès en GET est interdit.";
		return false;
	}

}
