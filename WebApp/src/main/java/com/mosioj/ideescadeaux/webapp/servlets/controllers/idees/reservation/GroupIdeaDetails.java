package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/detail_du_groupe")
public class GroupIdeaDetails extends IdeesCadeauxGetServlet<BookingGroupInteraction> {

    private static final long serialVersionUID = -2188278918134412556L;
    private static final Logger logger = LogManager.getLogger(GroupIdeaDetails.class);

    public static final String GROUP_ID_PARAM = "groupid";

    public static final String VIEW_PAGE_URL = "/protected/detail_du_groupe.jsp";
    public static final String GET_PAGE_WITH_GROUP_ID = "/protected/detail_du_groupe?groupid=";

    /**
     * Class constructor.
     */
    public GroupIdeaDetails() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        IdeaGroup group = policy.getGroupId();
        logger.debug("Getting details for idea group " + group + "...");
        Idee idee = IdeesRepository.getIdeaFromGroup(group.getId()).orElseThrow(SQLException::new);

        // Suppression des notif's si y'en a
        NotificationsRepository.getNotifications(thisOne.id,
                                                 NotificationType.GROUP_IDEA_SUGGESTION,
                                                 ParameterName.GROUP_ID,
                                                 group.getId()).forEach(NotificationsRepository::remove);

        request.setAttribute("idee", idee);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
    }

}
