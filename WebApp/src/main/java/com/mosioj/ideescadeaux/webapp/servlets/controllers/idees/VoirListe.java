package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/protected/voir_liste")
public class VoirListe extends MesListes {

    private static final long serialVersionUID = -5233551522645668356L;
    public static final String USER_ID_PARAM = "id";
    public static final String PROTECTED_VOIR_LIST = "/protected/voir_liste";

    /**
     * Class constructor.
     */
    public VoirListe() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
        List<User> ids = new ArrayList<>();
        User user = policy.getUser();
        ids.add(user);
        fillsUserIdeas(thisOne, ids);
        return ids;
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest req) {
        return 1;
    }

    @Override
    protected String getCallingURL() {
        return PROTECTED_VOIR_LIST.substring(1);
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest req) {
        return "";
    }

}
