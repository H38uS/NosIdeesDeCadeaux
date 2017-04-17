package com.mosioj.model.table;

import static com.mosioj.model.table.columns.NotificationsColumns.ID;
import static com.mosioj.model.table.columns.NotificationsColumns.OWNER;
import static com.mosioj.model.table.columns.NotificationsColumns.TEXT;
import static com.mosioj.model.table.columns.NotificationsColumns.TYPE;

import static com.mosioj.model.table.columns.NotificationParametersColumns.NOTIFICATION_ID;
import static com.mosioj.model.table.columns.NotificationParametersColumns.PARAMETER_NAME;
import static com.mosioj.model.table.columns.NotificationParametersColumns.PARAMETER_VALUE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Notification;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class Notifications extends Table {

	public static final String TABLE_NAME = "NOTIFICATIONS";
	public static final String TABLE_PARAMS = "NOTIFICATION_PARAMETERS";

	private final Logger logger = LogManager.getLogger(Notifications.class);

	private final static Lock MUTEX = new ReentrantLock(true);

	/**
	 * Save and send a notification.
	 * 
	 * @param userId The user id that will receive this notification.
	 * @param notif The notification.
	 * @throws SQLException
	 */
	public void addNotification(int userId, AbstractNotification notif) {

		logger.info(MessageFormat.format("Creating notification {0} for user {1}", notif.getType(), userId));
		int id = -1;

		// Insertion en base
		// TODO récupérer si on doit le faire
		MUTEX.lock();
		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(getDb(), "insert into notifications (owner, text, type) values (?, ?, ?)");
			ps.bindParameters(userId, notif.getText(), notif.getType());
			ps.execute();

			id = getDb().selectInt("select max(" + ID + ") from " + TABLE_NAME + " where " + OWNER + " = ?", userId);

		} catch (SQLException e) {
			logger.error("Error while creating " + notif.getClass() + " : " + e.getMessage());
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		if (id > 0) {
			Map<String, String> notifParams = notif.getParameters();
			for (String key : notifParams.keySet()) {
				try {
					ps = new PreparedStatementIdKdo(getDb(),
													MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?, ?, ?)",
																			TABLE_PARAMS,
																			NOTIFICATION_ID,
																			PARAMETER_NAME,
																			PARAMETER_VALUE));
					ps.bindParameters(id, key, notifParams.get(key));
					ps.execute();

				} catch (SQLException e) {
					logger.error("Error while creating " + notif.getClass() + " : " + e.getMessage());
				} finally {
					if (ps != null) {
						ps.close();
					}
				}
			}
		}
		MUTEX.unlock();

		// Envoie de la notification par email si besoin
		// TODO récupérer si on doit le faire
		notif.sendEmail(""); // TODO récupérer l'email
	}

	/**
	 * 
	 * @param userId
	 * @param notif
	 */
	public void removeAllType(int userId, AbstractNotification notif) {

		logger.info(MessageFormat.format("Delete notification {0} for user {1}", notif.getType(), userId));

		try {
			getDb().executeUpdate("delete from NOTIFICATIONS where owner = ? and type = ?", userId, notif.getType());
			getDb().executeUpdate(MessageFormat.format(	"delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
														NOTIFICATION_ID,
														ID,
														TABLE_NAME));
		} catch (SQLException e) {
			logger.error("Error while deleting " + notif.getClass() + " : " + e.getMessage());
		}
	}

	public void remove(int notificationId) throws SQLException {
		logger.debug(MessageFormat.format("Suppression de la notification {0}", notificationId));
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ", TABLE_NAME, ID), notificationId);
	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<Notification> getUserNotifications(int userId) throws SQLException {
		String query = MessageFormat.format("select {0}, {1}, {2}, {4} from {3} where {4} = ? ", ID, TEXT, TYPE, TABLE_NAME, OWNER);
		return getNotificationFromQuery(query, userId);
	}

	/**
	 * Technical method.
	 * 
	 * @param query
	 * @param userId
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	private List<Notification> getNotificationFromQuery(String query, Object... parameters) throws SQLException {

		PreparedStatementIdKdo ps = null;
		List<Notification> notifications = new ArrayList<Notification>();

		try {
			ps = new PreparedStatementIdKdo(getDb(), query);
			ps.bindParameters(parameters);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					notifications.add(new com.mosioj.model.Notification(res.getInt(ID.name()),
																		res.getInt(OWNER.name()),
																		res.getString(TYPE.name()),
																		res.getString(TEXT.name())));
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return notifications;
	}

	/**
	 * 
	 * @param userId
	 * @return The number of notification this user has.
	 */
	public int getUserNotificationCount(int userId) throws SQLException {
		return getDb().selectInt(MessageFormat.format("select count(*) from {0} where {1} = ?", TABLE_NAME, OWNER), userId);
	}

	/**
	 * 
	 * @param userId
	 * @param notif
	 * @return True if and only if the user has already receive this notification.
	 * @throws SQLException
	 */
	public boolean hasNotification(int userId, AbstractNotification notif) throws SQLException {

		Map<String, String> parameters = notif.getParameters();

		StringBuilder query = new StringBuilder();
		query.append("select 1 ");
		query.append(MessageFormat.format("from {0} n ", TABLE_NAME));

		Object[] queryParameters = new Object[parameters.keySet().size() * 2 + 2];
		int i = 0;

		for (String key : parameters.keySet()) {
			query.append(MessageFormat.format("inner join {0} p{1} ", TABLE_PARAMS, i));
			query.append(MessageFormat.format("on n.{0} = p{2}.{1} ", ID, NOTIFICATION_ID, i));
			query.append(MessageFormat.format("and p{0}.{1} = ? ", i, PARAMETER_NAME));
			query.append(MessageFormat.format("and p{0}.{1} = ? ", i, PARAMETER_VALUE));

			queryParameters[i++] = key;
			queryParameters[i++] = parameters.get(key);
		}

		query.append(MessageFormat.format("where n.{0} = ? ", TYPE));
		query.append(MessageFormat.format("  and n.{0} = ? ", OWNER));

		queryParameters[i++] = notif.getType();
		queryParameters[i++] = userId;

		logger.trace(query);

		return getDb().doesReturnRows(query.toString(), queryParameters);
	}

	/**
	 * 
	 * @param notifId
	 * @return The notification corresponding to this id.
	 * @throws SQLException
	 */
	public Notification getNotification(int notifId) throws SQLException {
		String query = MessageFormat.format("select {0}, {1}, {2}, {4} from {3} where {0} = ? ", ID, TEXT, TYPE, TABLE_NAME, OWNER);
		List<Notification> notifs = getNotificationFromQuery(query, notifId);
		return notifs.size() == 0 ? null : notifs.get(0);
	}
}
