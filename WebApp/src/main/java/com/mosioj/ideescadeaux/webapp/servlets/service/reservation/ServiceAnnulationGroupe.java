package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroupContent;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
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

        IdeaGroup group = policy.getGroup();
        logger.info("Annulation de la participation de {} au groupe {}.", thisOne, group.getId());
        boolean isThereSomeoneRemaining = GroupIdeaRepository.removeUserFromGroup(thisOne, group);

        // On a forcément une idée pour un groupe... Sinon grosse erreur !!
        Idee idee = IdeesRepository.getIdeaFromGroup(group).orElseThrow(SQLException::new);

        if (isThereSomeoneRemaining) {
            // On supprime les notif's qu'on a reçu sur ce groupe
            NotificationsRepository.terminator()
                                   .whereOwner(thisOne)
                                   .whereGroupIdea(group)
                                   .terminates();

            // On supprime toutes les notifcations que les autres ont reçu de nous sur ce groupe
            NotificationsRepository.terminator()
                                   .whereUser(thisOne)
                                   .whereGroupIdea(group)
                                   .terminates();

            // Ajout d'une notification de départ à tous ceux qui sont encore dans le groupe
            final Notification leftGroup = NType.LEAVE_GROUP.with(thisOne, idee, group);
            group.getShares()
                 .parallelStream()
                 .map(IdeaGroupContent::getUser)
                 .forEach(leftGroup::sendItTo);
        } else {
            // Suppression des anciennes notifications du groupe
            NotificationsRepository.terminator().whereGroupIdea(group).terminates();
            // Suppression de la réservation par groupe
            IdeesRepository.toutDereserver(idee);
        }

        buildResponse(response, ServiceResponse.ok(isThereSomeoneRemaining, thisOne));
    }
}
