package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.Relation;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends AbstractListes<Relation, NetworkAccess> {

    private static final long serialVersionUID = 9147880158497428623L;

    public static final String USER_ID_PARAM = "id";
    public static final String SELF_VIEW = "/protected/afficher_reseau";
    public static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

    /**
     * Class constructor.
     */
    public AfficherReseau() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        User friend = policy.getUser();
        int userId = thisOne.id;

        if (userId == friend.id) {
            // Uniquement sur notre compte
            request.setAttribute("demandes", UserRelationRequestsRepository.getRequests(userId));
            request.setAttribute("suggestions", UserRelationsSuggestionRepository.hasReceivedSuggestion(userId));
        }

        request.setAttribute("id", friend.id);
        request.setAttribute("name", friend.getMyDName());

        HttpSession session = request.getSession();
        Object accepted = session.getAttribute("accepted");
        if (accepted != null) {
            request.setAttribute("accepted", accepted);
            session.removeAttribute("accepted");
        }

        super.ideesKDoGET(request, response);
    }

    @Override
    protected String getViewPageURL() {
        return DISPATCH_URL;
    }

    @Override
    protected String getCallingURL() {
        return SELF_VIEW.substring(1);
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest req) {
        return "&" + USER_ID_PARAM + "=" + policy.getUser().id;
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest req) {
        return UserRelationsRepository.getRelationsCount(policy.getUser());
    }

    @Override
    protected List<Relation> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
        return UserRelationsRepository.getRelations(policy.getUser().id,
                                                    firstRow,
                                                    helper.getMaxNumberOfResults());
    }

}
