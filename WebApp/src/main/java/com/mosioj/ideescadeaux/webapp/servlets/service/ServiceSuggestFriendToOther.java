package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.PossibleSuggestion;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NetworkAccess;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/protected/service/possible_relation_suggestions")
public class ServiceSuggestFriendToOther extends ServiceGet<NetworkAccess> {

    protected static final String USER_PARAMETER = "id";

    public ServiceSuggestFriendToOther() {
        super(new NetworkAccess(USER_PARAMETER));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {

        // Parameters
        User suggestToUser = policy.getUser();
        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();

        // Listing all possible users
        List<User> toBeSuggested = UserRelationsRepository.getAllUsersInRelationNotInOtherNetwork(thisOne,
                                                                                                  suggestToUser,
                                                                                                  userNameOrEmail,
                                                                                                  0,
                                                                                                  50);
        toBeSuggested.remove(suggestToUser);

        // Enriching them
        List<PossibleSuggestion> possibleSuggestions = toBeSuggested.stream().map(PossibleSuggestion::new).peek(ps -> {
            final User u = ps.possibleSuggestion;
            if (UserRelationsSuggestionRepository.hasReceivedSuggestionOf(suggestToUser.id, u.id)) {
                ps.isPossible = false;
                ps.reasonIfNotPossible = MessageFormat.format("{0} a déjà reçu une suggestion pour {1}.",
                                                              suggestToUser.getName(),
                                                              u.getName());
            }
            if (UserRelationRequestsRepository.associationExists(suggestToUser, u)) {
                ps.isPossible = false;
                ps.reasonIfNotPossible = MessageFormat.format("{0} a déjà envoyé une demande à {1}.",
                                                              suggestToUser.getName(),
                                                              u.getName());
            }
            if (UserRelationRequestsRepository.associationExists(u, suggestToUser)) {
                ps.isPossible = false;
                ps.reasonIfNotPossible = MessageFormat.format("{0} a déjà envoyé une demande à {1}.",
                                                              u.getName(),
                                                              suggestToUser.getName());
            }
        }).collect(Collectors.toList());

        // Returning the response object
        buildResponse(response, ServiceResponse.ok(possibleSuggestions, thisOne));
    }

}
