package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
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

import static com.mosioj.ideescadeaux.core.model.notifications.NType.NEW_IDEA_BIRTHDAY_SOON;

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
        Idee idea = policy.getIdea();

        // On r??cup??re les personnes qui ont r??serv?? avant la restoration (peut les supprimer)
        Set<User> bookers = new HashSet<>(idea.getBookers());

        // Restoring the idea
        logger.debug("Restoration de l'id??e {}, avec r??servation ? => {}", idea, restoreBooking);
        IdeesRepository.restoreIdea(idea, restoreBooking);

        // Suppression des notifications pass??es de suppression d'id??e
        // On va avoir une notification de restoration dans tous les cas
        List<Notification> bookingRemoveNotifs = NotificationsRepository.fetcher()
                                                                        .whereType(NType.BOOKED_REMOVE)
                                                                        .whereIdea(idea)
                                                                        .fetch();
        bookingRemoveNotifs.forEach(NotificationsRepository::remove);

        // Si l'anniversaire est proche, on ajoute tous les amis !
        if (thisOne.getNbDaysBeforeBirthday() < User.NB_DAYS_BEFORE_BIRTHDAY) {
            final Notification birthdayIsSoon = NEW_IDEA_BIRTHDAY_SOON.with(thisOne, idea);
            UserRelationsRepository.getAllUsersInRelation(thisOne).forEach(birthdayIsSoon::sendItTo);
        } else {
            // On notifie toutes les personnes qui avaient r??serv?? ou re??u une notification "BOOKING_REMOVE"
            bookers.addAll(bookingRemoveNotifs.stream()
                                              .map(Notification::getOwner)
                                              .collect(Collectors.toSet()));
            final Notification notifIdeaRestored = NType.IDEA_RESTORED.with(thisOne, idea);
            bookers.forEach(notifIdeaRestored::sendItTo);
        }

        // Sending back the OK response
        buildResponse(response, ServiceResponse.ok(thisOne));
    }

}
