package com.mosioj.ideescadeaux.notifications;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.notifications.instance.NotifAdministration;

public class NotificationFactory {

    private static final Logger LOGGER = LogManager.getLogger(NotificationFactory.class);

    private NotificationFactory() {
    }

    /**
     * @param id The notification id.
     * @param owner The owner.
     * @param type The notification type.
     * @param text The notification text.
     * @param readOn When it has been read.
     * @param isUnread If it has been read.
     * @return A new notification object based on the database content.
     */
    public static AbstractNotification buildIt(int id,
                                               int owner,
                                               String type,
                                               String text,
                                               Timestamp creationTime,
                                               boolean isUnread,
                                               Timestamp readOn,
                                               Map<ParameterName, Object> params) throws SQLException {

        if (NotificationsRepository.NOTIF_TYPE_ADMIN_ERROR.equals(type)) {
            return new NotifAdministration(id, owner, text, params, creationTime, isUnread, readOn);
        } else if (NotificationsRepository.NOTIF_TYPE_NEW_INSCRIPTION.equals(type)) {
            return new NotifAdministration(id, owner, text, params, creationTime, isUnread, readOn);
        }

        NotificationType t = NotificationType.valueOf(type);
        Class<? extends AbstractNotification> clazz = t.getNotificationClassName();

        AbstractNotification notification;
        try {
            LOGGER.debug(MessageFormat.format(
                    "Creation d''une notification en mémoire de type {0} avec les paramètres: {1} pour le user {2}",
                    type,
                    params,
                    owner));
            Constructor<? extends AbstractNotification> ctor = clazz.getConstructor(int.class,
                                                                                    int.class,
                                                                                    String.class,
                                                                                    Timestamp.class,
                                                                                    boolean.class,
                                                                                    Timestamp.class,
                                                                                    Map.class);
            notification = ctor.newInstance(id, owner, text, creationTime, isUnread, readOn, params);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return notification;
    }
}
