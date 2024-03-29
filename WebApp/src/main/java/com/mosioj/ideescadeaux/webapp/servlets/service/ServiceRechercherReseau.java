package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppUser;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/protected/service/rechercher_reseau")
public class ServiceRechercherReseau extends ServiceGet<NetworkAccess> {

    /** Class logger */
    private static final Logger LOGGER = LogManager.getLogger(ServiceRechercherReseau.class);

    /** The page helper. */
    private static final ListResultWithPagesHelper PAGES_HELPER = ListResultWithPagesHelper.withDefaultMax();

    private static final String USER_ID_PARAM = "id";
    protected static final String SEARCH_USER_PARAM = "looking_for";

    /** Class constructor. */
    public ServiceRechercherReseau() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Getting the parameter
        final String nameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM, false).toLowerCase();
        LOGGER.trace("Received: {}", nameOrEmail);

        // Loading the list
        Set<User> raw = UserRelationsRepository.getAllUsersInRelation(policy.getUser(),
                                                                      nameOrEmail,
                                                                      PAGES_HELPER.getFirstRow(request),
                                                                      PAGES_HELPER.getMaxNumberOfResults());

        List<DecoratedWebAppUser> relations = raw.stream()
                                                 .map(r -> new DecoratedWebAppUser(r, thisOne))
                                                 .collect(Collectors.toList());
        LOGGER.trace("Matched: {}", relations);

        final List<Page> pages = PAGES_HELPER.getPages(request, relations.size(), this::getTotalNumberOfRecords);
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, relations), thisOne));
    }

    /**
     * @param request The http request. May contain parameters.
     * @return The total number of records that will be produced when fetching the entire list.
     */
    protected int getTotalNumberOfRecords(HttpServletRequest request) {
        final String userNameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM, false);
        return UserRelationsRepository.getAllUsersInRelationCount(policy.getUser(), userNameOrEmail);
    }
}
