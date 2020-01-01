package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.core.model.entities.Relation;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/rechercher_reseau")
public class RechercherReseau extends AbstractListes<Relation, NetworkAccess> {

    private static final long serialVersionUID = 9147880158497428623L;

    private static final String USER_ID_PARAM = "id";
    private static final String SEARCH_USER_PARAM = "looking_for";

    /**
     * Class constructor.
     */
    public RechercherReseau() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        String nameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM);
        User networkOwner = policy.getUser();
        request.setAttribute("id", networkOwner.id);
        request.setAttribute("name", networkOwner.getName());
        request.setAttribute(SEARCH_USER_PARAM, nameOrEmail);

        super.ideesKDoGET(request, response);
    }

    @Override
    protected String getViewPageURL() {
        return AfficherReseau.DISPATCH_URL;
    }

    @Override
    protected String getCallingURL() {
        return "protected/rechercher_reseau";
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest req) {
        return "&" +
               USER_ID_PARAM +
               "=" +
               policy.getUser().id +
               "&" +
               SEARCH_USER_PARAM +
               "=" +
               ParametersUtils.readAndEscape(req, SEARCH_USER_PARAM);
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
        return UserRelationsRepository.getRelationsCount(policy.getUser().id,
                                                         ParametersUtils.readAndEscape(req, SEARCH_USER_PARAM));
    }

    @Override
    protected List<Relation> getDisplayedEntities(int firstRow, HttpServletRequest request) throws SQLException {

        String nameOrEmail = ParametersUtils.readAndEscape(request, SEARCH_USER_PARAM);
        List<Relation> relations = UserRelationsRepository.getRelations(policy.getUser().id,
                                                                        nameOrEmail,
                                                                        firstRow,
                                                                        maxNumberOfResults);
        int userId = thisOne.id;

        // Ajout du flag network
        for (Relation r : relations) {
            if (UserRelationsRepository.associationExists(r.getSecond().id, userId)) {
                r.secondIsInMyNetwork = true;
            } else {
                User other = r.getSecond();
                if (UserRelationRequestsRepository.associationExists(userId, other.id)) {
                    other.freeComment = "Vous avez déjà envoyé une demande à " + other.getName();
                }
            }
        }

        return relations;
    }

}
