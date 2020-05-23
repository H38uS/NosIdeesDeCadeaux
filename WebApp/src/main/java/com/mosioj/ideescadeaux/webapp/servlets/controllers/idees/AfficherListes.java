package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends AbstractUserListes<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 1209953017190072617L;

    public static final String AFFICHER_LISTES = "/protected/afficher_listes";
    protected static final String NAME_OR_EMAIL = "name";

    /**
     * Class constructor.
     */
    public AfficherListes() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    protected List<OwnerIdeas> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
        String nameOrEmail = ParametersUtils.readNameOrEmail(req, NAME_OR_EMAIL);
        List<User> users = new ArrayList<>();
        int MAX = maxNumberOfResults;
        User connected = thisOne;
        if (connected.matchNameOrEmail(nameOrEmail)) {
            users.add(connected);
            MAX--;
        }
        users.addAll(UserRelationsRepository.getAllUsersInRelation(connected.id, nameOrEmail, firstRow, MAX));
        return getPersonIdeasFromUser(users);
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest request) throws SQLException {
        User user = thisOne;
        String nameOrEmail = ParametersUtils.readNameOrEmail(request, NAME_OR_EMAIL);
        int size = UserRelationsRepository.getAllUsersInRelationCount(user, nameOrEmail);
        if (user.matchNameOrEmail(nameOrEmail)) {
            return size + 1;
        }
        return size;
    }

    @Override
    protected String getCallingURL() {
        return AFFICHER_LISTES.substring(1);
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest req) {
        return "&" +
               NAME_OR_EMAIL +
               "=" +
               ParametersUtils.readAndEscape(req, NAME_OR_EMAIL);
    }

}
