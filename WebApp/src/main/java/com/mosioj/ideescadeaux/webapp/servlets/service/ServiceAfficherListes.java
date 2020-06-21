package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
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

@WebServlet("/protected/service/afficher_listes")
public class ServiceAfficherListes extends ServiceGet<AllAccessToPostAndGet> {

    /** The page helper. */
    private static final ListResultWithPagesHelper PAGES_HELPER = ListResultWithPagesHelper.withDefaultMax();

    /** The parameter name. */
    protected static final String NAME_OR_EMAIL = "name";

    /** Class constructor. */
    public ServiceAfficherListes() {
        super(new AllAccessToPostAndGet());
    }

    // FIXME : faut ajouter les bandeaux de recherche de liste dans afficher liste dans le JS : pour tout charger d'un coup
    // Cela permettra aussi de finir le refactor et supprimer les entités de VoirListe et AfficherListe

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Getting parameters
        int firstRow = PAGES_HELPER.getFirstRow(request);
        String nameOrEmail = ParametersUtils.readNameOrEmail(request, NAME_OR_EMAIL);

        // Getting the user list
        List<User> users = UserRelationsRepository.getAllUsersInRelation(thisOne.id,
                                                                         nameOrEmail,
                                                                         firstRow,
                                                                         PAGES_HELPER.getMaxNumberOfResults());
        if (thisOne.matchNameOrEmail(nameOrEmail)) {
            users.add(0, thisOne);
            if (users.size() > PAGES_HELPER.getMaxNumberOfResults()) {
                users.remove(users.size() - 1);
            }
        }

        // Adding the list of ideas for each user
        final List<OwnerIdeas> foundUsers = IdeaLogic.getPersonsIdeasFromUsers(thisOne, users, device);

        // Wrapping them with the page mechanism
        final List<Page> pages = PAGES_HELPER.getPages(request, foundUsers.size(), this::getTotalNumberOfRecords);

        // Sending the response
        buildResponse(response, ServiceResponse.ok(PagedResponse.from(pages, foundUsers), isAdmin(request), thisOne));
    }

    /**
     * @param request The http request. May contain parameters.
     * @return The total number of records that will be produced when fetching the entire list.
     */
    protected int getTotalNumberOfRecords(HttpServletRequest request) {
        String nameOrEmail = ParametersUtils.readNameOrEmail(request, NAME_OR_EMAIL);
        int size = UserRelationsRepository.getAllUsersInRelationCount(thisOne, nameOrEmail);
        if (thisOne.matchNameOrEmail(nameOrEmail)) {
            return size + 1;
        }
        return size;
    }
}
