package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository.getIdeaFromGroup;
import static com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository.findNotificationMatching;

@WebServlet("/protected/service/group/suggest")
public class ServiceSuggestIdeaGroup extends ServiceGetAndPost<BookingGroupInteraction> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceSuggestIdeaGroup.class);

    /** Service group identifier parameter. */
    public static final String GROUP_ID_PARAM = "groupid";

    /**
     * Class constructor.
     */
    public ServiceSuggestIdeaGroup() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        IdeaGroup group = policy.getGroupId();
        Idee idee = getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);
        logger.debug("Getting potential suggestions for the group {}...", group.getId());

        // Tous les utilisateurs qui peuvent être intéressés, filtré sur ceux qui n'ont pas la notif
        final NotifGroupSuggestion suggestion = new NotifGroupSuggestion(thisOne, group.getId(), idee);
        List<User> candidates = IdeesRepository.getPotentialGroupUser(group.getId(), thisOne.id)
                                               .stream()
                                               .filter(u -> findNotificationMatching(u.id, suggestion).isEmpty())
                                               .collect(Collectors.toList());

        logger.debug(MessageFormat.format("Potential users: {0}", candidates));
        buildResponse(response, ServiceResponse.ok(candidates, isAdmin(request), thisOne));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        // Récupération des différents paramètres
        final Map<String, String[]> params = request.getParameterMap();
        final String[] usersParam = params.getOrDefault("userIds[]", ArrayUtils.toArray());
        final Stream<String> userIds = Stream.of(usersParam);
        logger.debug("Selected user's ids : {}", Arrays.toString(usersParam));

        // The notification we are trying to send
        IdeaGroup group = policy.getGroupId();
        Idee idee = getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);
        final NotifGroupSuggestion suggestion = new NotifGroupSuggestion(thisOne, group.getId(), idee);

        // Récupération des utilisateurs qui n'ont pas encore cette notification...
        List<User> selectedUsers = userIds.map(Integer::new)
                                          .map(UsersRepository::getUser)
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .filter(u -> findNotificationMatching(u.id, suggestion).isEmpty())
                                          .collect(Collectors.toList());

        // ... Envoie de la notification
        selectedUsers.forEach(u -> NotificationsRepository.addNotification(u.id, suggestion));
        logger.debug("Notification sent to: {}", selectedUsers);

        buildResponse(response, ServiceResponse.ok(selectedUsers, isAdmin(request), thisOne));
    }

}