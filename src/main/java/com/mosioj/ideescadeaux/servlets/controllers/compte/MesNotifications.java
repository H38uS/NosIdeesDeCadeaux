package com.mosioj.ideescadeaux.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.model.repositories.ParentRelationshipRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.notifications.ChildNotifications;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/mes_notifications")
public class MesNotifications extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(MesNotifications.class);

    private static final long serialVersionUID = -5988235378202921870L;
    public static final String URL = "/protected/mes_notifications";
    private static final String VIEW_URL = "/protected/mes_notifications.jsp";

    public MesNotifications() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        int userId = thisOne.id;
        req.setAttribute("unread_notifications", NotificationsRepository.getUserUnReadNotifications(userId));
        req.setAttribute("read_notifications", NotificationsRepository.getUserReadNotifications(userId));

        List<ChildNotifications> children = new ArrayList<>();
        ParentRelationshipRepository.getChildren(userId).forEach(c -> {
            try {
                children.add(new ChildNotifications(c, NotificationsRepository.getUserNotifications(c.id)));
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        });
        req.setAttribute("child_notifications", children);

        RootingsUtils.rootToPage(VIEW_URL, req, resp);
    }
}
