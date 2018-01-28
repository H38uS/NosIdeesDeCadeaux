package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;

public abstract class AbstractUserListes extends AbstractListes<User> {

	private static final long serialVersionUID = 1638868138216657989L;
	private static final Logger LOGGER = LogManager.getLogger(AbstractUserListes.class);

	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	public AbstractUserListes(SecurityPolicy policy) {
		super(policy, 6);
	}

	@Override
	protected String getViewPageURL() {
		return VIEW_PAGE_URL;
	}

	protected void fillsUserIdeas(int userId, List<User> ids) throws SQLException {
		LOGGER.trace("Getting all ideas for all users...");
		for (User user : ids) {
			List<Idee> ownerIdeas = idees.getOwnerIdeas(user.id);
			for (Idee idee : ownerIdeas) {
				
				idee.hasComment = comments.getNbComments(idee.getId()) > 0;
				idee.hasQuestion = questions.getNbQuestions(idee.getId()) > 0;
				
				User surpriseBy = idee.getSurpriseBy();
				if (surpriseBy != null) {
					if (surpriseBy.id == userId) {
						idee.displayClass = "booked_by_me_idea";
					} else {
						idee.displayClass = "booked_by_others_idea";
					}
				} else if (idee.isBooked()) {
					if (idee.getBookingOwner() != null) {
						if (idee.getBookingOwner().id == userId) {
							// Réservé par soit !
							idee.displayClass = "booked_by_me_idea";
						} else {
							// Réservé par un autre
							idee.displayClass = "booked_by_others_idea";
						}
					} else {
						// Réserver par un groupe
						idee.displayClass = "shared_booking_idea";
					}
				} else if (idee.isPartiallyBooked()) {
					idee.displayClass = "shared_booking_idea";
				}
				// Sinon, on laisse la class par défaut
			}
			user.addIdeas(ownerIdeas);
		}
	}
}
