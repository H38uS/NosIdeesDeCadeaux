package com.mosioj.notifications;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.utils.database.DataSourceIdKDo;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class NotificationManager {

	private static final DataSourceIdKDo DB = new DataSourceIdKDo();

	/**
	 * Class logger.
	 */
	private final Logger logger = LogManager.getLogger(NotificationManager.class);

	/**
	 * Save and send a notification.
	 * 
	 * @param userId The user id that will receive this notification.
	 * @param notif The notification.
	 * @throws SQLException
	 */
	public void addNotification(int userId, Notification notif) {

		logger.info(MessageFormat.format("Creating notification {0} for user {1}", notif.getType(), userId));

		// Insertion en base
		// TODO récupérer si on doit le faire
		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(DB, "insert into notifications (owner, text, type) values (?, ?, ?)");
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

	public void remove(int userId, Notification notif) {

		logger.info(MessageFormat.format("Delete notification {0} for user {1}", notif.getType(), userId));

		try {
			DB.executeUpdate("delete from NOTIFICATIONS where owner = ? and type = ?", userId, notif.getType());
		} catch (SQLException e) {
			logger.error("Error while deleting " + notif.getClass() + " : " + e.getMessage());
		}

	}

}
