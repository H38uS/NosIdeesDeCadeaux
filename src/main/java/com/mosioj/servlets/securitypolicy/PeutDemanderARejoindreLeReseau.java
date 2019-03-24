package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;

public class PeutDemanderARejoindreLeReseau extends AllAccessToPostAndGet implements SecurityPolicy {

	private static final Logger logger = LogManager.getLogger(PeutDemanderARejoindreLeReseau.class);

	private final UserRelations userRelations;
	private final UserRelationRequests userRelationRequests;

	private final String userParameter;

	/**
	 * 
	 * @param userRelations
	 * @param userRelationRequests
	 */
	public PeutDemanderARejoindreLeReseau(	UserRelations userRelations,
											UserRelationRequests userRelationRequests,
											String userParameter) {
		this.userRelations = userRelations;
		this.userRelationRequests = userRelationRequests;
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException, NotLoggedInException {

		try {
			int userId = ParametersUtils.getConnectedUser(request).id;

			// Y a-t-il un utilisateur ?
			Integer toBeSentTo = ParametersUtils.readInt(request, userParameter);
			if (toBeSentTo == null) {
				lastReason = "Aucun utilisateur trouvé en paramètre.";
				return false;
			}

			if (toBeSentTo == userId || userRelations.associationExists(toBeSentTo, userId)) {
				lastReason = "Vous faites déjà parti du même réseau.";
				return false;
			}

			if (userRelationRequests.associationExists(userId, toBeSentTo)) {
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
