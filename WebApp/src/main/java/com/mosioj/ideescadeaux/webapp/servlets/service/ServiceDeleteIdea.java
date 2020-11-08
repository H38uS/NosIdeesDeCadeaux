package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifBookedRemove;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.model.notifications.instance.param.NotifUserIdParam;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<AbstractNotification> notifications = NotificationsRepository.getNotification(ParameterName.IDEA_ID,
                                                                                           idea.getId());

        // Pour les notifications qui sont lié à un user (pas le owner)
        // On les ajoute au set à notifier
        Set<Integer> toBeNotified = idea.getBookers().stream().map(User::getId).collect(Collectors.toSet());
        notifications.parallelStream().filter(n -> n instanceof NotifUserIdParam)
                     .map(n -> (NotifUserIdParam) n)
                     .map(NotifUserIdParam::getUserIdParam)
                     .filter(userId -> userId != idea.getOwner().getId())
                     .forEach(toBeNotified::add);

        // Envoie des notifications
        final NotifBookedRemove notifBookingRemove = new NotifBookedRemove(idea, idea.owner.getName());
        toBeNotified.forEach(id -> NotificationsRepository.addNotification(id, notifBookingRemove));

        // Suppression des anciennes notifications
        notifications.forEach(NotificationsRepository::remove);

        // Deleting previous questions about whether it is up to date...
        IsUpToDateQuestionsRepository.deleteAssociations(idea.getId());

        int userId = thisOne.id;
        IdeesRepository.remove(idea);

        if (!IdeesRepository.hasIdeas(userId)) {
            NotificationsRepository.addNotification(userId, new NotifNoIdea());
        }

        buildResponse(response, ServiceResponse.ok(isAdmin(request), thisOne));
    }
}
