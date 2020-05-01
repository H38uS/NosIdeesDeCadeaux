package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PeutResoudreDemandesAmis extends SecurityPolicy {

    private static final Logger logger = LogManager.getLogger(PeutResoudreDemandesAmis.class);

    /**
     * The user answer for each friendship request.
     */
    private Map<Integer, Boolean> choiceParameters;

    private boolean hasAccess(HttpServletRequest request) {
        try {
            Map<String, String[]> params = request.getParameterMap();
            for (String key : params.keySet()) {
                if (!key.startsWith("choix_")) {
                    continue;
                }
                if (params.get(key).length > 0) {
                    choiceParameters.put(Integer.parseInt(key.substring("choix_".length())),
                                         "Accepter".equals(params.get(key)[0]));
                }
            }

            if (choiceParameters.isEmpty()) {
                lastReason = "Aucun utilisateur trouvé en paramètre.";
                return false;
            }

            int userId = connectedUser.id;

            // Only keep users that exists
            choiceParameters.keySet().retainAll(choiceParameters.keySet()
                                                                .parallelStream()
                                                                .map(UsersRepository::getUser)
                                                                .filter(Optional::isPresent)
                                                                .map(Optional::get)
                                                                .map(User::getId)
                                                                .collect(Collectors.toSet()));

            for (int user : choiceParameters.keySet()) {
                if (user == userId) {
                    lastReason = "Vous ne pouvez pas être ami avec vous-même...";
                    return false;
                }
                if (UserRelationsRepository.associationExists(userId, user)) {
                    lastReason = "Vous êtes déjà ami avec l'une des personnes...";
                    return false;
                }
                if (!UserRelationRequestsRepository.associationExists(user, userId)) {
                    lastReason = "Au moins une personne ne vous a jamais fait de demande...";
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            logger.error("Unable to process the security check: " + e.getMessage());
            lastReason = "Une erreur est survenue lors de la vérification des droits. Veuillez réessayer, ou contacter l'administrateur.";
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return hasAccess(request);
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return hasAccess(request);
    }

    /**
     * @return the choiceParameters
     */
    public Map<Integer, Boolean> getChoiceParameters() {
        return choiceParameters;
    }

    @Override
    public void reset() {
        choiceParameters = new HashMap<>();
    }

}
