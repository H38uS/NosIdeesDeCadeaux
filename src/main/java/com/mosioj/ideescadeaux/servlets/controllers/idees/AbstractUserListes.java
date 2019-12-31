package com.mosioj.ideescadeaux.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.List;

import com.mosioj.ideescadeaux.model.repositories.IdeesRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;

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
     * @param connectedUser The connected user.
     * @param ids  We want the ideas of those user list.
     */
    protected void fillsUserIdeas(User connectedUser, List<User> ids) throws SQLException {
        logger.trace("Getting all ideas for all users...");
        for (User user : ids) {
            List<Idee> ownerIdeas = IdeesRepository.getIdeasOf(user.id);
            for (Idee idee : ownerIdeas) {
                IdeesRepository.fillAUserIdea(connectedUser, idee, device);
            }
            user.setIdeas(ownerIdeas);
        }
    }
}
