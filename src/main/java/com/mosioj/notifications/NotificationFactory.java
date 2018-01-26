package com.mosioj.notifications;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

public class NotificationFactory {

	private NotificationFactory() {
	}

	/**
	 * 
	 * @param id
	 * @param owner
	 * @param type
	 * @param text
	 * @param notifParams
	 * @return A new notification object based on the database content.
	 * @throws SQLException
	 */
	public static AbstractNotification buildIt(	int id,
												int owner,
												String type,
												String text,
												Timestamp creationTime,
												Map<ParameterName, Object> params) throws SQLException {

		NotificationType t = NotificationType.valueOf(type);
		Class<? extends AbstractNotification> clazz = t.getNotificationClassName();

		AbstractNotification notification = null;
		try {
			Constructor<? extends AbstractNotification> ctor = clazz.getConstructor(int.class, int.class, String.class, Timestamp.class, Map.class);
			notification = ctor.newInstance(new Object[] { id, owner, text, creationTime, params });
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return notification;
	}
}
