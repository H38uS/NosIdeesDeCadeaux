package com.mosioj.ideescadeaux.servlets.securitypolicy;

import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

public final class PeutDemanderARejoindreLeReseau extends SecurityPolicy implements UserSecurityChecker {

	private static final Logger logger = LogManager.getLogger(PeutDemanderARejoindreLeReseau.class);

	private final String userParameter;
	private User potentialFriend;

	/**
	 */
	public PeutDemanderARejoindreLeReseau(String userParameter) {
		this.userParameter = userParameter;
	}

	private boolean hasAccess(HttpServletRequest request) throws SQLException {

		try {
			int userId = connectedUser.id;

			// Y a-t-il un utilisateur ?
			// FIXME : 2 pour toutes les polices qui récupèrent un paramètre, vérifier que ça existe en base (e.g. pour
			// USERS)
			Optional<Integer> toBeSentTo = readInt(request, userParameter);
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

			potentialFriend = model.users.getUser(toBeSentTo.get());
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

	@Override
	public User getUser() {
		return potentialFriend;
	}

}
