package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/protected/service/je_la_veux_encore")
public class ServiceJeLaVeuxEncore extends ServicePost<IdeaModification> {

    /** Class logger */
    private static final Logger LOGGER = LogManager.getLogger(ServiceJeLaVeuxEncore.class);

    /** The service parameter. */
    public static final String IDEA_ID_PARAM = "idee";

    /** Class constructor. */
    public ServiceJeLaVeuxEncore() {
        super(new IdeaModification(IDEA_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        LOGGER.info("Suppression des réservations de l'idée {} par {}.", idea.getId(), thisOne);

        // On notifie toujours ceux qui ont réservé
        Set<User> toBeNotified = new HashSet<>(idea.getBookers());

        // Puis si l'anniversaire est proche, tous les amis !
        if (thisOne.getNbDaysBeforeBirthday() < User.NB_DAYS_BEFORE_BIRTHDAY) {
            toBeNotified.addAll(UserRelationsRepository.getAllUsersInRelation(thisOne));
        }

        // Notification
        final Notification NeedAgainNotification = NType.RECURENT_IDEA_UNBOOK.with(thisOne, idea);
        toBeNotified.forEach(NeedAgainNotification::sendItTo);

        // On supprime les réservations
        IdeesRepository.toutDereserverSaufSousReservation(idea);

        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
