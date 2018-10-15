package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;

public abstract class AbstractUserListes<P extends SecurityPolicy> extends AbstractListes<User, P> {

	private static final long serialVersionUID = 1638868138216657989L;
	private static final Logger logger = LogManager.getLogger(AbstractUserListes.class);

	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	public AbstractUserListes(P policy) {
		super(policy, 6);
	}

	@Override
	protected String getViewPageURL() {
		return VIEW_PAGE_URL;
	}

	/**
	 * Fills in the user ideas.
	 * 
	 * @param userId The connected user.
	 * @param ids We want the ideas of those user list.
	 * @throws SQLException
	 */
	protected void fillsUserIdeas(int userId, List<User> ids) throws SQLException {

		Set<Integer> ideas = notif.getIdeasOnWhichWeHaveAskedIfUpToDate(userId);

		logger.trace("Getting all ideas for all users...");
		for (User user : ids) {
			List<Idee> ownerIdeas = idees.getIdeasOf(user.id);
			for (Idee idee : ownerIdeas) {
				fillAUserIdea(userId, idee, ideas.contains(idee.getId()));
			}
			user.addIdeas(ownerIdeas);
		}
	}
}
