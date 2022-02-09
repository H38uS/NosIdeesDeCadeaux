package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.RelationRequest;
import com.mosioj.ideescadeaux.core.model.entities.RelationSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/protected/service/suggestion_amis")
public class ServiceSuggestionAmis extends ServiceGetAndPost<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(ServiceSuggestionAmis.class);

    public ServiceSuggestionAmis() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Getting the current user's suggestion
        List<RelationSuggestion> suggestions = UserRelationsSuggestionRepository.getUserSuggestions(thisOne);

        // Writing answer
        buildResponse(response, ServiceResponse.ok(suggestions, thisOne));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        final int userId = thisOne.id;
        final List<String> selectedRejectedList = request.getParameterMap()
                                                         .values()
                                                         .stream()
                                                         .filter(array -> array.length > 1)
                                                         .filter(array -> "true".equals(array[1]))
                                                         .map(array -> {
                                                             // ["selected_13", false] or ["rejected_15", true]
                                                             // => rejected_15 (keep only first identifier + for the true
                                                             return array[0];
                                                         }).collect(Collectors.toList());

        final List<Integer> toBeAsked = getCollect(selectedRejectedList, "selected_");
        final List<Integer> toIgnore = getCollect(selectedRejectedList, "rejected_");
        final List<String> errors = new ArrayList<>();

        toBeAsked.stream()
                 .map(UsersRepository::getUser)
                 .filter(Optional::isPresent)
                 .map(Optional::get)
                 .forEach(u -> {
                     UserRelationsSuggestionRepository.removeIfExists(userId, u.id);
                     if (u.id == userId || UserRelationsRepository.associationExists(u, thisOne)) {
                         errors.add(MessageFormat.format("{0} fait déjà parti de votre réseau.", u.getName()));
                         return;
                     }
                     if (UserRelationRequestsRepository.associationExists(thisOne, u)) {
                         errors.add(MessageFormat.format("Vous avez déjà envoyé une demande à {0}.", u.getName()));
                         return;
                     }
                     // On ajoute l'association
                     HibernateUtil.saveit(new RelationRequest(thisOne, u));
                     logger.info("Envoie d'une demande de {} à {}.", thisOne, u);
                 });

        toIgnore.forEach(i -> {
            UserRelationsSuggestionRepository.removeIfExists(userId, i);
            logger.info("Suppression de la suggestion de {} à {}.", i, thisOne);
        });

        if (errors.isEmpty()) {
            buildResponse(response,
                          ServiceResponse.ok(UserRelationsSuggestionRepository.getUserSuggestions(thisOne),
                                             thisOne));
        } else {
            buildResponse(response, ServiceResponse.ko(errors, thisOne));
        }
    }

    /**
     * @param selectedRejectedList The initial list (e.g. "selected_13", or "rejected_42" for instance).
     * @param jsPrefix             The prefix before the integer value (e.g. "selected_").
     * @return The list of integer after removing the prefix value.
     */
    private List<Integer> getCollect(List<String> selectedRejectedList, String jsPrefix) {
        return selectedRejectedList.stream()
                                   .filter(s -> s.startsWith(jsPrefix))
                                   .map(s -> s.substring(jsPrefix.length()))
                                   .map(Integer::parseInt)
                                   .collect(Collectors.toList());
    }
}
