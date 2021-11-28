package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@WebServlet("/protected/afficher_reseau")
public class AfficherReseau extends IdeesCadeauxGetServlet<NetworkAccess> {

    private static final long serialVersionUID = 9147880158497428623L;

    public static final String USER_ID_PARAM = "id";
    public static final String SELF_VIEW = "/protected/afficher_reseau";
    public static final String DISPATCH_URL = "/protected/afficher_reseau.jsp";

    /** Class constructor. */
    public AfficherReseau() {
        super(new NetworkAccess(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        User friend = policy.getUser();
        int userId = thisOne.id;

        if (userId == friend.id) {
            // Uniquement sur notre compte
            request.setAttribute("demandes", UserRelationRequestsRepository.getRequests(userId));
            request.setAttribute("suggestions", UserRelationsSuggestionRepository.getUserSuggestions(thisOne));
        }

        request.setAttribute("id", friend.id);
        request.setAttribute("name", friend.getMyDName());
        request.setAttribute("looking_for", ParametersUtils.readAndEscape(request, "looking_for"));

        HttpSession session = request.getSession();
        Object accepted = session.getAttribute("accepted");
        if (accepted != null) {
            request.setAttribute("accepted", accepted);
            session.removeAttribute("accepted");
        }

        RootingsUtils.rootToPage(DISPATCH_URL, request, response);
    }

}
