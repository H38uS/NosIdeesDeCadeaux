package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository.getIdeaFromGroup;
import static com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository.findNotificationMatching;

@WebServlet("/protected/suggerer_groupe_idee")
public class SuggestGroupIdea extends IdeesCadeauxServlet<BookingGroupInteraction> {

    private static final Logger logger = LogManager.getLogger(SuggestGroupIdea.class);
    private static final long serialVersionUID = 5094570058900475394L;
    public static final String GROUP_ID_PARAM = "groupid";
    public static final String VIEW_URL = "/protected/suggerer_groupe_idee";
    public static final String VIEW_PAGE_URL = "/protected/suggest_group_idea.jsp";

    /**
     * Class constructor.
     */
    public SuggestGroupIdea() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        IdeaGroup group = policy.getGroupId();
        logger.debug("Getting details for idea group {}...", group.getId());

        Idee idee = getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

        // Tous les utilisateurs qui peuvent être intéressés
        List<User> candidates = IdeesRepository.getPotentialGroupUser(group.getId(), thisOne.id);

        // On conserve que ceux qui n'ont pas encore de notifications
        NotifGroupSuggestion suggestion = new NotifGroupSuggestion(thisOne, group.getId(), idee);
        candidates = candidates.stream()
                               .filter(toRemove -> findNotificationMatching(toRemove.id, suggestion).size() == 0)
                               .collect(Collectors.toList());

        logger.debug(MessageFormat.format("Potential users: {0}", candidates));

        request.setAttribute("candidates", candidates);
        request.setAttribute("idee", idee);
        request.setAttribute("group", group);

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        IdeaGroup group = policy.getGroupId();
        Idee idee = getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

        List<Integer> selectedUsers = new ArrayList<>();
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            if (values.length == 1 && "on".equals(values[0])) {
                try {
                    int user = Integer.parseInt(key);
                    selectedUsers.add(user);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        List<User> successTo = new ArrayList<>();
        logger.debug("Selected users : " + selectedUsers);
        selectedUsers.stream()
                     .map(UsersRepository::getUser)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .forEach(u -> suggestTheGroupTo(group, idee, u, successTo));

        request.setAttribute("sent_to_users", successTo);
        ideesKDoGET(request, response);
    }

    private void suggestTheGroupTo(IdeaGroup group, Idee idea, User user, List<User> successTo) {
        NotifGroupSuggestion suggestion = new NotifGroupSuggestion(thisOne, group.getId(), idea);
        final List<AbstractNotification> existingNotif = findNotificationMatching(user.id, suggestion);
        if (existingNotif.size() == 0) {
            NotificationsRepository.addNotification(user.id, suggestion);
        }
        successTo.add(user);
    }

}
