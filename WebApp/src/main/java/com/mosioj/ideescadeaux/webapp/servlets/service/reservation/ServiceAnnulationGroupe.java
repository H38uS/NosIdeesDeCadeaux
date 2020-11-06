package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupEvolution;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/group/annulation")
public class ServiceAnnulationGroupe extends ServicePost<BookingGroupInteraction> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceAnnulationGroupe.class);

    /** Group parameter */
    public static final String GROUP_ID_PARAM = "groupid";

    /**
     * Class constructor.
     */
    public ServiceAnnulationGroupe() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        IdeaGroup group = policy.getGroupId();
        logger.info("Annulation de la participation de {} au groupe {}.", thisOne, group.getId());
        boolean isThereSomeoneRemaining = GroupIdeaRepository.removeUserFromGroup(thisOne, group.getId());

        if (isThereSomeoneRemaining) {
            // On supprime les notif's qu'on a reçu sur ce groupe
            NotificationsRepository.getNotification(ParameterName.GROUP_ID, group.getId()).parallelStream()
                                   .filter(n -> thisOne.equals(n.getOwner()))
                                   .forEach(NotificationsRepository::remove);

            // On supprime toutes les notifcations que les autres ont reçu de nous sur ce groupe
            NotificationsRepository.getNotification(ParameterName.GROUP_ID, group.getId()).parallelStream()
                                   .filter(n -> n.getParameters()
                                                 .getOrDefault(ParameterName.USER_ID, "")
                                                 // the parameter map is giving Strings...
                                                 .equals(String.valueOf(thisOne.getId())))
                                   .forEach(NotificationsRepository::remove);

            // On a forcément une idée pour un groupe... Sinon grosse erreur !!
            Idee idee = IdeesRepository.getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

            // Ajout d'une notification de départ à tous ceux qui sont encore dans le groupe
            final NotifGroupEvolution notif = new NotifGroupEvolution(thisOne, group.getId(), idee, false);
            group.getShares()
                 .parallelStream()
                 .forEach(s -> NotificationsRepository.addNotification(s.getUser().id, notif));

            NotificationsRepository.getNotification(ParameterName.GROUP_ID, group.getId()).parallelStream()
                                   .filter(n -> n.getType().equals(NotificationType.GROUP_IDEA_SUGGESTION.name()))
                                   .filter(n -> thisOne.equals(n.getOwner()))
                                   .forEach(NotificationsRepository::remove);
        } else {
            // Suppression des anciennes notifications
            NotificationsRepository.getNotification(ParameterName.GROUP_ID, group.getId()).parallelStream()
                                   .forEach(NotificationsRepository::remove);
        }

        buildResponse(response, ServiceResponse.ok(isThereSomeoneRemaining, isAdmin(request), thisOne));
    }
}
