package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebServlet("/protected/service/delete_idea")
public class ServiceDeleteIdea extends ServicePost<IdeaModification> {

    private static final Logger logger = LogManager.getLogger(ServiceDeleteIdea.class);
    private static final long serialVersionUID = 2642366164643542379L;
    public static final String IDEE_ID_PARAM = "ideeId";

    public ServiceDeleteIdea() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        // Reading parameters
        logger.debug(MessageFormat.format("Deleting idea {0}.", idea.getId()));

        List<Notification> notifications = NotificationsRepository.fetcher().whereIdea(idea).fetch();

        // Pour les notifications qui sont lié à un user (pas le owner)
        // On les ajoute au set à notifier
        Set<User> toBeNotified = new HashSet<>(idea.getBookers());
        notifications.parallelStream()
                     .map(Notification::getUserParameter)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .filter(user -> !user.equals(idea.getOwner()))
                     .forEach(toBeNotified::add);

        // Envoie des notifications
        final Notification notifBookingRemove = NType.BOOKED_REMOVE.with(thisOne, idea);
        toBeNotified.forEach(notifBookingRemove::sendItTo);

        // Suppression des anciennes notifications
        notifications.forEach(NotificationsRepository::remove);

        // Deleting previous questions about whether it is up to date...
        IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

        // Suppression de l'idée
        IdeesRepository.remove(idea);

        if (!IdeesRepository.hasIdeas(thisOne.id)) {
            NType.NO_IDEA.buildDefault().sendItTo(thisOne);
        }

        buildResponse(response, ServiceResponse.ok(thisOne));
    }
}
