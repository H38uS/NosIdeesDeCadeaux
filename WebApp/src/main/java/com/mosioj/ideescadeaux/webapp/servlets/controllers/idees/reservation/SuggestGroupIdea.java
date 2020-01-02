package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import static com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository.getIdeaWithoutEnrichmentFromGroup;

@WebServlet("/protected/suggerer_groupe_idee")
public class SuggestGroupIdea extends IdeesCadeauxGetAndPostServlet<BookingGroupInteraction> {

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
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        IdeaGroup group = policy.getGroupId();
        logger.debug("Getting details for idea group " + group.getId() + "...");

        Idee idee = getIdeaWithoutEnrichmentFromGroup(group.getId()).orElseThrow(SQLException::new);
        User user = thisOne;
        IdeesCadeauxServlet.fillAUserIdea(user, idee, device.isMobile());

        List<User> potentialGroupUser = IdeesRepository.getPotentialGroupUser(group.getId(), user.id);
        logger.debug(MessageFormat.format("Potential users: {0}", potentialGroupUser));
        List<User> removable = new ArrayList<>();
        for (User toRemove : potentialGroupUser) {
            NotifGroupSuggestion suggestion = new NotifGroupSuggestion(user, group.getId(), idee);
            NotificationsRepository.hasNotification(toRemove.id, suggestion)
                                   .filter(has -> has)
                                   .ifPresent(has -> removable.add(toRemove));
        }
        potentialGroupUser.removeAll(removable);

        request.setAttribute("candidates", potentialGroupUser);
        request.setAttribute("idee", idee);
        request.setAttribute("group", group);

        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        IdeaGroup group = policy.getGroupId();
        Idee idee = getIdeaWithoutEnrichmentFromGroup(group.getId()).orElseThrow(SQLException::new);
        IdeesCadeauxServlet.fillAUserIdea(thisOne, idee, device.isMobile());

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
        NotificationsRepository.hasNotification(user.id, suggestion)
                               .filter(has -> !has)
                               .ifPresent(n -> NotificationsRepository.addNotification(user.id,
                                                                                       suggestion));
        successTo.add(user);
    }

}
