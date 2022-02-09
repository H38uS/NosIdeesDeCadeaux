package com.mosioj.ideescadeaux.webapp.servlets.service;


import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/protected/service/resoudre_demande_ami")
public class ServiceGestionDemandeAmi extends ServiceGetAndPost<AllAccessToPostAndGet> {

    /** Class Logger */
    private static final Logger logger = LogManager.getLogger(ServiceGestionDemandeAmi.class);

    public ServiceGestionDemandeAmi() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {
        buildResponse(response,
                      ServiceResponse.ok(UserRelationRequestsRepository.getRequests(thisOne.id),
                                         isAdmin(request),
                                         thisOne));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        final List<String> selectedRejectedList = request.getParameterMap()
                                                         .values()
                                                         .stream()
                                                         .filter(array -> array.length > 1)
                                                         .filter(array -> "true".equals(array[1]))
                                                         .map(array -> {
                                                             // ["acc_choix_13", false] or [ref_choix_15", true]
                                                             // => ref_choix_15 (keep only first identifier + for the true
                                                             return array[0];
                                                         }).collect(Collectors.toList());

        List<User> accepted = getCollect(selectedRejectedList, "acc_choix_");
        accepted.forEach(u -> {
            logger.info("Approbation de la demande par {} de l'utilisateur {}.", thisOne, u);
            UserRelationsRepository.addAssociation(u.id, thisOne.id);
            UserRelationRequestsRepository.cancelRequest(u, thisOne);
            NType.ACCEPTED_FRIENDSHIP.with(thisOne).sendItTo(u);
            clearNotifications(u);
        });
        getCollect(selectedRejectedList, "ref_choix_").forEach(u -> {
            logger.info("Refus de la demande par {} de l'utilisateur {}.", thisOne.id, u);
            UserRelationRequestsRepository.cancelRequest(u, thisOne);
            NType.REJECTED_FRIENDSHIP.with(thisOne).sendItTo(u);
            clearNotifications(u);
        });

        buildResponse(response, ServiceResponse.ok(accepted.stream().map(User::getName), isAdmin(request), thisOne));
    }

    private void clearNotifications(User fromUser) {

        // Si fromUser avait supprimé sa relation avec nous
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.FRIENDSHIP_DROPPED)
                               .whereUser(fromUser)
                               .terminates();
        // Si nous avions supprimé notre relation avec fromUser
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.FRIENDSHIP_DROPPED)
                               .whereUser(thisOne)
                               .terminates();
        // Si fromUser avait refusé notre demande
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.REJECTED_FRIENDSHIP)
                               .whereUser(fromUser)
                               .terminates();
        // Si nous avions refusé une demande précédente de fromUser
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.REJECTED_FRIENDSHIP)
                               .whereUser(thisOne)
                               .terminates();

        // Suppression des suggestions d'amitiés entre ces deux personnes
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(fromUser)
                               .terminates();
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(thisOne)
                               .terminates();

        // Suppression des demandes d'amis qu'on lui a faites
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.NEW_FRIENSHIP_REQUEST)
                               .whereUser(thisOne)
                               .terminates();
        // Ou qu'il nous a fait
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_FRIENSHIP_REQUEST)
                               .whereUser(fromUser)
                               .terminates();
    }

    /**
     * @param selectedRejectedList The initial list (e.g. "selected_13", or "rejected_42" for instance).
     * @param jsPrefix             The prefix before the integer value (e.g. "selected_").
     * @return The list of User after removing the prefix value.
     */
    private List<User> getCollect(List<String> selectedRejectedList, String jsPrefix) {
        return selectedRejectedList.stream()
                                   .filter(s -> s.startsWith(jsPrefix))
                                   .map(s -> s.substring(jsPrefix.length()))
                                   .map(Integer::parseInt)
                                   .filter(i -> i != thisOne.id)
                                   .map(UsersRepository::getUser)
                                   .filter(Optional::isPresent)
                                   .map(Optional::get)
                                   .filter(u -> !UserRelationsRepository.associationExists(thisOne.id, u.id))
                                   .filter(u -> UserRelationRequestsRepository.associationExists(u.id, thisOne.id))
                                   .collect(Collectors.toList());
    }
}
