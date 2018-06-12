package com.mosioj.notifications;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.table.Notifications;
import com.mosioj.notifications.instance.NotifErrorOccured;

public class NotificationFactory {

	private static final Logger LOGGER = LogManager.getLogger(NotificationFactory.class);

	private NotificationFactory() {
	}

	/**
	 * 
	 * @param id
	 * @param owner
	 * @param type
	 * @param text
	 * @param readOn
	 * @param isUnread
	 * @param notifParams
	 * @return A new notification object based on the database content.
	 * @throws SQLException
	 */
	public static AbstractNotification buildIt(	int id,
												int owner,
												String type,
												String text,
												Timestamp creationTime,
												boolean isUnread,
												Timestamp readOn,
												Map<ParameterName, Object> params) throws SQLException {

		if (Notifications.NOTIF_TYPE_ADMIN_ERROR.equals(type)) {
			return new NotifErrorOccured(id, owner, text, params, creationTime, isUnread, readOn);
		}

		NotificationType t = NotificationType.valueOf(type);
		Class<? extends AbstractNotification> clazz = t.getNotificationClassName();

		AbstractNotification notification = null;
		try {
			LOGGER.debug(MessageFormat.format("Creation d''une notification  de type {0} avec les paramètres: {1}", type, params));
			Constructor<? extends AbstractNotification> ctor = clazz.getConstructor(int.class,
																					int.class,
																					String.class,
																					Timestamp.class,
																					boolean.class,
																					Timestamp.class,
																					Map.class);
			notification = ctor.newInstance(new Object[] { id, owner, text, creationTime, isUnread, readOn, params });
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return notification;
	}
}
