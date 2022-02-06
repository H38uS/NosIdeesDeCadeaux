package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/protected/suggestion_rejoindre_reseau")
public class SuggestionRejoindreReseau extends IdeesCadeauxPostServlet<NetworkAccess> {

    private static final long serialVersionUID = 5529157183937072477L;
    private static final String USER_PARAMETER = "userId";
    private static final String URL_SUCCESS = "suggerer_relations_succes.jsp";
    private static final String URL_ERROR = "suggerer_relations_error.jsp";

    public SuggestionRejoindreReseau() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        User suggestTo = policy.getUser();

        List<Integer> suggestedUsers = getSelectedChoices(request.getParameterMap(), "selected_");

        // Persist suggestions
        List<User> sent = suggestedUsers.stream()
                                        .map(UsersRepository::getUser)
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(Collectors.toList());
        sent.forEach(u -> UserRelationsSuggestionRepository.newSuggestion(thisOne, suggestTo, u));

        if (sent.size() > 0) {
            // Send a notification
            NType.NEW_RELATION_SUGGESTION.with(thisOne).sendItTo(suggestTo);
            request.setAttribute("user", UsersRepository.getUser(suggestTo.id).orElseThrow(SQLException::new));
            request.setAttribute("users", sent);
            RootingsUtils.rootToPage(URL_SUCCESS, request, response);
        } else {
            request.setAttribute("user", UsersRepository.getUser(suggestTo.id).orElseThrow(SQLException::new));
            RootingsUtils.rootToPage(URL_ERROR, request, response);
        }
    }

}
