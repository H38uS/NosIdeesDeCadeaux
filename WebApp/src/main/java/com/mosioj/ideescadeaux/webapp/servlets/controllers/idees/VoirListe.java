package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        // Récupération du paramètre
        Optional<Integer> ideaId = ParametersUtils.readIntFromSession(request, "added_idea_id");

        // S'il existe, on nettoie la session
        ideaId.ifPresent(i -> request.getSession().removeAttribute("added_idea_id"));

        // 1. Transformation en idée
        // 2. Filtre sur celle dont on est le owner
        // 3. Ajout à la requête si cela existe
        ideaId.flatMap(this::enrichIdeaIfFound)
              .filter(i -> i.owner == thisOne)
              .ifPresent(i -> request.setAttribute("idee", i));

        super.ideesKDoGET(request, response);
    }

    /**
     *
     * @param ideaId The idea id.
     * @return The corresponding enriched idea if it exists.
     */
    private Optional<Idee> enrichIdeaIfFound(int ideaId) {
        return getIdeaAndEnrichIt(ideaId);
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
