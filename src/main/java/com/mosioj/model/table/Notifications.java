package com.mosioj.model.table;

import static com.mosioj.model.table.columns.NotificationsColumns.ID;
import static com.mosioj.model.table.columns.NotificationsColumns.OWNER;
import static com.mosioj.model.table.columns.NotificationsColumns.TEXT;
import static com.mosioj.model.table.columns.NotificationsColumns.TYPE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Notification;
import com.mosioj.notifications.AbstractNotification;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class Notifications extends Table {

	public static final String TABLE_NAME = "NOTIFICATIONS";
	private final Logger logger = LogManager.getLogger(Notifications.class);

	/**
	 * Save and send a notification.
	 * 
	 * @param userId The user id that will receive this notification.
	 * @param notif The notification.
	 * @throws SQLException
	 */
	public void addNotification(int userId, AbstractNotification notif) {

		logger.info(MessageFormat.format("Creating notification {0} for user {1}", notif.getType(), userId));

		// Insertion en base
		// TODO récupérer si on doit le faire
		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(getDb(), "insert into notifications (owner, text, type) values (?, ?, ?)");
			ps.bindParameters(userId, notif.getText(), notif.getType());
			ps.execute();
		} catch (SQLException e) {
			logger.error("Error while creating " + notif.getClass() + " : " + e.getMessage());
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		// Envoie de la notification par email si besoin
		// TODO récupérer si on doit le faire
		notif.sendEmail(""); // TODO récupérer l'email
	}

	public void remove(int userId, AbstractNotification notif) {

		logger.info(MessageFormat.format("Delete notification {0} for user {1}", notif.getType(), userId));

		try {
			getDb().executeUpdate("delete from NOTIFICATIONS where owner = ? and type = ?", userId, notif.getType());
		} catch (SQLException e) {
			logger.error("Error while deleting " + notif.getClass() + " : " + e.getMessage());
		}

	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<Notification> getUserNotifications(int userId) throws SQLException {

		List<Notification> notifications = new ArrayList<Notification>();

		PreparedStatementIdKdo ps = null;
		String query = MessageFormat.format("select {0}, {1}, {2} from {3} where {4} = ? ", ID, TEXT, TYPE, TABLE_NAME, OWNER);

		try {
			ps = new PreparedStatementIdKdo(getDb(), query);
			ps.bindParameters(userId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					notifications.add(new com.mosioj.model.Notification(res.getInt(ID.name()),
																		userId,
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
}
