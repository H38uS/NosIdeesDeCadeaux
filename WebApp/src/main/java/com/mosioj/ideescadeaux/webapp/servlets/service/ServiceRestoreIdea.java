package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaModifiedWhenBirthdayIsSoon;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaRestored;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.RestoreIdea;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/protected/service/idee/restore")
public class ServiceRestoreIdea extends ServicePost<RestoreIdea> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceRestoreIdea.class);

    /** The idea identifier parameter. */
    public static final String IDEE_ID_PARAM = "idee";

    /** The parameter telling whether to restore the booking information of not. */
    public static final String RESTORE_BOOKING = "restoreBooking";

    /** Class constructor. */
    public ServiceRestoreIdea() {
        super(new RestoreIdea(IDEE_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Parameters
        boolean restoreBooking = "true".equalsIgnoreCase(ParametersUtils.readAndEscape(request, RESTORE_BOOKING));
        Idee idee = policy.getIdea();

        // On récupère les personnes qui ont réservé avant la restoration (peut les supprimer)
        Set<User> bookers = new HashSet<>(idee.getBookers());

        // Restoring the idea
        logger.debug("Restoration de l'idée {}, avec réservation ? => {}", idee, restoreBooking);
        IdeesRepository.restoreIdea(idee, restoreBooking);

        // Suppression des notifications passées de suppression d'idée
        // On va avoir une notification de restoration dans tous les cas
        List<AbstractNotification> bookingRemoveNotifs = NotificationsRepository.getNotifications(NotificationType.BOOKED_REMOVE,
                                                                                                  ParameterName.IDEA_ID,
                                                                                                  idee.getId());
        bookingRemoveNotifs.forEach(NotificationsRepository::remove);

        // Si l'anniversaire est proche, on ajoute tous les amis !
        if (thisOne.getNbDaysBeforeBirthday() < NotifIdeaModifiedWhenBirthdayIsSoon.NB_DAYS_BEFORE_BIRTHDAY) {
            final NotifIdeaModifiedWhenBirthdayIsSoon birthdayIsSoon = new NotifIdeaModifiedWhenBirthdayIsSoon(thisOne,
                                                                                                               idee,
                                                                                                               true);
            UserRelationsRepository.getAllUsersInRelation(thisOne)
                                   .forEach(u -> NotificationsRepository.addNotification(u.getId(), birthdayIsSoon));
        } else {
            // On notifie toutes les personnes qui avaient réservé ou reçu une notification "BOOKING_REMOVE"
            bookers.addAll(bookingRemoveNotifs.stream().map(AbstractNotification::getOwner).collect(Collectors.toSet()));
            final NotifIdeaRestored notifIdeaRestored = new NotifIdeaRestored(idee);
            bookers.forEach(u -> NotificationsRepository.addNotification(u.getId(), notifIdeaRestored));
        }

        // Sending back the OK response
        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }

}
