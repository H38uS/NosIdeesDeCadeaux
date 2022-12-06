package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * A policy to make sure we can interact with an idea.
 *
 * @author Jordan Mosio
 */
public class NotificationModification extends SecurityPolicy {

    private static final Logger logger = LogManager.getLogger(NotificationModification.class);

    /**
     * Defines the string used in HttpServletRequest to retrieve the notification id.
     */
    private final String notifParameter;

    private Notification notif;

    /**
     * @param notifParameter Defines the string used in HttpServletRequest to retrieve the notification id.
     */
    public NotificationModification(String notifParameter) {
        this.notifParameter = notifParameter;
    }

    /**
     * @param request The http request.
     * @return True if the current user can interact with the idea.
     */
    private boolean canModifyNotification(HttpServletRequest request) throws SQLException {

        notif = ParametersUtils.readInt(request, notifParameter)
                               .flatMap(NotificationsRepository::getNotification)
                               .orElse(null);
        if (notif == null) {
            lastReason = "Aucune notification trouvée en paramètre.";
            return false;
        }

        int userId = connectedUser.id;

        final boolean isMe = connectedUser.equals(notif.getOwner());
        final User notifOwner = notif.getOwner();
        boolean res = isMe || ParentRelationshipRepository.getChildren(userId).contains(notifOwner);
        if (!res) {
            lastReason = "Vous ne pouvez modifier que vos notifications ou celles de vos enfants.";
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canModifyNotification(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return canModifyNotification(request);
        } catch (SQLException e) {
            logger.error("Cannot process checking, SQLException: " + e);
            return false;
        }
    }

    /**
     * @return The notification id, or null if the checks fail.
     */
    public Notification getNotification() {
        return notif;
    }

    @Override
    public void reset() {
        notif = null;
    }

}
