package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/protected/mes_listes")
public class MesListes extends AbstractUserListes<NetworkAccess> {

    private static final long serialVersionUID = -1774633803227715931L;
    public static final String PROTECTED_MES_LISTES = "/protected/mes_listes";

    /**
     * Class constructor.
     */
    public MesListes(NetworkAccess policy) {
        super(policy);
    }

    @Override
    protected List<OwnerIdeas> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
        User user = thisOne;
        List<User> users = new ArrayList<>();
        if (firstRow == 0) {
            users.add(user);
        }
        users.addAll(UserRelationsRepository.getAllUsersInRelation(user, firstRow, maxNumberOfResults));
        return getPersonIdeasFromUser(users);
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
        // On ne se compte pas, car on apparait nécessairement dans la première page (et cela n'affecte pas le max)
        return UserRelationsRepository.getRelationsCount(thisOne);
    }

    @Override
    protected String getCallingURL() {
        return PROTECTED_MES_LISTES.substring(1);
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest req) {
        return "";
    }

}
