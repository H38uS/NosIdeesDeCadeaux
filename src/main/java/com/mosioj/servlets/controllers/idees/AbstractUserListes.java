package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.List;

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
	 * @param user The connected user.
	 * @param ids We want the ideas of those user list.
	 * @throws SQLException
	 */
	protected void fillsUserIdeas(User connectedUser, List<User> ids) throws SQLException {
		logger.trace("Getting all ideas for all users...");
		for (User user : ids) {
			List<Idee> ownerIdeas = model.idees.getIdeasOf(user.id);
			for (Idee idee : ownerIdeas) {
				model.idees.fillAUserIdea(connectedUser, idee, device);
			}
			user.setIdeas(ownerIdeas);
		}
	}
}
