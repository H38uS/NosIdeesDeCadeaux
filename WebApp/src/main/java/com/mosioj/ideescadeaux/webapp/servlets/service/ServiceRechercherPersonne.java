package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

import static com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository.associationExists;

@WebServlet("/protected/service/rechercher_personne")
public class ServiceRechercherPersonne extends ServiceGet<AllAccessToPostAndGet> {

    /** The page helper. */
    private static final ListResultWithPagesHelper PAGES_HELPER = ListResultWithPagesHelper.withDefaultMax();

    /**
     * Class constructor.
     */
    public ServiceRechercherPersonne() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Getting parameters
        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
        String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);

        // Building the initial list
        List<User> foundUsers = UsersRepository.getUsers(userNameOrEmail,
                                                         thisOne.id,
                                                         onlyNonFriend,
                                                         PAGES_HELPER.getFirstRow(request),
                                                         PAGES_HELPER.getMaxNumberOfResults());

        if (!onlyNonFriend) {
            foundUsers.forEach(user -> user.withIsInMyNetwork(associationExists(user.id, thisOne.id)));
        }

        for (User user : foundUsers) {
            if (UserRelationRequestsRepository.associationExists(thisOne.id, user.id)) {
                user.freeComment = "Vous avez déjà envoyé une demande à " + user.getName();
            }
        }

        final List<Page> pages = PAGES_HELPER.getPages(request, foundUsers.size(), this::getTotalNumberOfRecords);
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, foundUsers), isAdmin(request), thisOne));
    }

    /**
     * The http request. May contain parameters.
     *
     * @return The total number of records that will be produced when fetching the entire list.
     */
    private int getTotalNumberOfRecords(HttpServletRequest request) {
        int userId = thisOne.id;
        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
        String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
        return UsersRepository.getTotalUsers(userNameOrEmail, userId, onlyNonFriend);
    }
}
