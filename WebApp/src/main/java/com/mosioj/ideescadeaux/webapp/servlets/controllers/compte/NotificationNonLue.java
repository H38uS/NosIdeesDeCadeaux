package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NotificationModification;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/notification_non_lue")
public class NotificationNonLue extends IdeesCadeauxGetServlet<NotificationModification> {

    private static final long serialVersionUID = -5381776220247069645L;
    private static final String NOTIFICATION_PARAMETER = "notif_id";

    public NotificationNonLue() {
        super(new NotificationModification(NOTIFICATION_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        NotificationsRepository.setUnread(policy.getNotification());
        RootingsUtils.redirectToPage(MesNotifications.URL, req, resp);
    }

}
