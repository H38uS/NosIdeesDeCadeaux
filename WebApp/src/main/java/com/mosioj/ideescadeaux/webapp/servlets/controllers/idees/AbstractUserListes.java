package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractUserListes<P extends SecurityPolicy> extends AbstractListes<OwnerIdeas, P> {

    private static final long serialVersionUID = 1638868138216657989L;
    private static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

    public AbstractUserListes(P policy) {
        super(policy, ListResultWithPagesHelper.with(6));
    }

    @Override
    protected String getViewPageURL() {
        return VIEW_PAGE_URL;
    }

    /**
     * Fills in the user ideas.
     *
     * @param users We want the ideas of those user list.
     * @return Those users as their ideas in a single list.
     */
    protected List<OwnerIdeas> getPersonIdeasFromUser(List<User> users) {
        return users.stream()
                    .map(u -> OwnerIdeas.from(u,
                                              IdeesRepository.getIdeasOf(u.id)
                                                             .stream()
                                                             .map(i -> new DecoratedWebAppIdea(i, thisOne, device))
                                                             .collect(Collectors.toList())))
                    .collect(Collectors.toList());
    }
}
