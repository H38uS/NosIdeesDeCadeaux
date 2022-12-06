package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@WebServlet("/protected/service/suggestion_rejoindre_reseau")
public class ServiceSuggestionRejoindreReseau extends ServicePost<NetworkAccess> {

    protected static final String USER_PARAMETER = "id";

    /**
     * Class constructor.
     */
    public ServiceSuggestionRejoindreReseau() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        User suggestTo = policy.getUser();

        // All the selected ones
        final List<User> selectedOnes = request.getParameterMap()
                                               .values()
                                               .stream()
                                               .filter(array -> array.length > 1)
                                               .filter(array -> "true".equals(array[1]))
                                               .map(array -> {
                                                   // ["selected_13", false] or ["selected_15", true]
                                                   // => selected_15 (keep only first identifier + for the true
                                                   return array[0];
                                               })
                                               .map(s -> s.substring("selected_".length()))
                                               .map(Integer::parseInt)
                                               .map(UsersRepository::getUser)
                                               .filter(Optional::isPresent)
                                               .map(Optional::get)
                                               .toList();

        // Persist suggestions
        selectedOnes.forEach(u -> UserRelationsSuggestionRepository.newSuggestion(thisOne, suggestTo, u));

        if (selectedOnes.size() > 0) {
            // Send a notification
            NType.NEW_RELATION_SUGGESTION.with(thisOne).sendItTo(suggestTo);
        }

        final String message = "Les utilisateurs suivants ont bien été ajouté: " + selectedOnes;
        buildResponse(response, ServiceResponse.ok(message, thisOne));
    }
}
