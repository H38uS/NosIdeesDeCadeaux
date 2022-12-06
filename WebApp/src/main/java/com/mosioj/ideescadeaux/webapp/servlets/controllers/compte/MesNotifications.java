package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.ChildNotifications;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/protected/mes_notifications")
public class MesNotifications extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    public static final String URL = "/protected/mes_notifications";
    private static final String VIEW_URL = "/protected/mes_notifications.jsp";

    public MesNotifications() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

        int userId = thisOne.id;
        req.setAttribute("unread_notifications", NotificationsRepository.getUserUnReadNotifications(thisOne));
        req.setAttribute("read_notifications", NotificationsRepository.getUserReadNotifications(thisOne));

        List<ChildNotifications> childNotif = ParentRelationshipRepository.getChildren(userId)
                                                                          .stream()
                                                                          .map(this::getChildNotifications)
                                                                          .collect(Collectors.toList());

        req.setAttribute("child_notifications", childNotif);
        RootingsUtils.rootToPage(VIEW_URL, req, resp);
    }

    /**
     * @param theChild The child user.
     * @return This user bounded to its notifications.
     */
    private ChildNotifications getChildNotifications(User theChild) {
        return new ChildNotifications(theChild, NotificationsRepository.fetcher().whereOwner(theChild).fetch());
    }
}
