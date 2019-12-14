package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationParametersColumns.NOTIFICATION_ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationParametersColumns.PARAMETER_NAME;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationParametersColumns.PARAMETER_VALUE;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.CREATION_DATE;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.IS_UNREAD;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.OWNER;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.READ_ON;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.TEXT;
import static com.mosioj.ideescadeaux.model.repositories.columns.NotificationsColumns.TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.notifications.NotificationFactory;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.utils.EmailSender;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdoInserter;

public class Notifications extends Table {

	public static final String TABLE_NAME = "NOTIFICATIONS";
	public static final String TABLE_PARAMS = "NOTIFICATION_PARAMETERS";

	public static final String NOTIF_TYPE_ADMIN_ERROR = "ADMIN_ERROR";
	public static final String NOTIF_TYPE_NEW_INSCRIPTION = "NEW_INSCRIPTION";

	private final Logger logger = LogManager.getLogger(Notifications.class);
	private final UserParameters userParameters = new UserParameters();

	private String urlTillProtectedPublic;

	public void setURL(String fullURL) {
		if (fullURL.contains("protected")) {
			urlTillProtectedPublic = fullURL.substring(0, fullURL.indexOf("protected"));
		}
		if (fullURL.contains("public")) {
			urlTillProtectedPublic = fullURL.substring(0, fullURL.indexOf("public"));
		}
	}

	/**
	 * Save and send a notification.
	 * 
	 * @param userId The user that will receive this notification.
	 * @param notif The notification.
	 * @return The id of the notification created, or -1 if none were created.
	 * @throws SQLException
	 */
	public int addNotification(int userId, AbstractNotification notif) throws SQLException {

		logger.info(MessageFormat.format("Creating notification {0} for user {1}", notif.getType(), userId));
		NotificationActivation activation = getActivationType(userId, notif);
		int id = -1;

		// Insertion en base
		if (activation == NotificationActivation.SITE || activation == NotificationActivation.EMAIL_SITE) {
			PreparedStatementIdKdoInserter ps = null;
			try {
				ps = new PreparedStatementIdKdoInserter(getDb(),
														MessageFormat.format(	"insert into {0} (owner, text, type, creation_date) values (?, ?, ?, now())",
																				TABLE_NAME));
				ps.bindParameters(userId, notif.getTextToInsert(), notif.getType());
				id = ps.executeUpdate();

			} catch (SQLException e) {
				logger.error("Error while creating " + notif.getClass() + " : " + e.getMessage());
			} finally {
				if (ps != null) {
					ps.close();
				}
			}

			if (id > 0) {
				logger.debug(MessageFormat.format("Creating parameters for notification {0}", id));
				Map<ParameterName, Object> notifParams = notif.getParameters();
				for (ParameterName key : notifParams.keySet()) {
					try {
						ps = new PreparedStatementIdKdoInserter(getDb(),
																MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?, ?, ?)",
																						TABLE_PARAMS,
																						NOTIFICATION_ID,
																						PARAMETER_NAME,
																						PARAMETER_VALUE));
						ps.bindParameters(id, key, notifParams.get(key).toString());
						ps.executeUpdate();
						ps.close();

					} finally {
						if (ps != null) {
							ps.close();
						}
					}
				}
			}
		}

		// Envoie de la notification par email si besoin
		if (activation == NotificationActivation.EMAIL || activation == NotificationActivation.EMAIL_SITE) {
			String email = getDb().selectString(MessageFormat.format(	"select {0} from {1} where {2} = ?",
																		UsersColumns.EMAIL,
																		Users.TABLE_NAME,
																		UsersColumns.ID),
												userId);
			notif.sendEmail(email, urlTillProtectedPublic);
		}

		return id;
	}

	/**
	 * 
	 * @param userId
	 * @param notif
	 * @return The activation type. Default is EMAIL_SITE.
	 */
	private NotificationActivation getActivationType(int userId, AbstractNotification notif) {
		try {
			return NotificationActivation.valueOf(userParameters.getParameter(userId, notif.getType()));
		} catch (SQLException e) {
			return NotificationActivation.EMAIL_SITE;
		}
	}

	/**
	 * 
	 * @param user
	 * @param notif
	 * @throws SQLException
	 */
	public void removeAllType(User user, NotificationType notifType) throws SQLException {

		logger.info(MessageFormat.format("Delete notification {0} for user {1}", notifType.name(), user));

		getDb().executeUpdate(	MessageFormat.format("delete from {0} where owner = ? and type = ?", TABLE_NAME),
								user.id,
								notifType.name());
		getDb().executeUpdate(MessageFormat.format(	"delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
													NOTIFICATION_ID,
													ID,
													TABLE_NAME));
	}

	public void remove(int notificationId) {
		logger.info(MessageFormat.format("Suppression de la notification {0}", notificationId));
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ", TABLE_NAME, ID), notificationId);
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ", TABLE_PARAMS, NOTIFICATION_ID), notificationId);
	}

	/**
	 * Deletes all notification that have the given owner, type and parameters.
	 * 
	 * @param owner
	 * @param type
	 * @param parameterName
	 * @param parameterValue
	 * @throws SQLException
	 */
	public void removeAllType(	User owner,
								NotificationType type,
								ParameterName parameterName,
								Object parameterValue) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("delete from {0} where ", TABLE_NAME));
		sb.append(MessageFormat.format(	" exists (select 1 from {0} p where p.{1} = {2} and p.{3} = ?  and p.{4} = ?) and {5} = ? and {6} = ?",
										TABLE_PARAMS,
										NOTIFICATION_ID,
										ID,
										PARAMETER_NAME,
										PARAMETER_VALUE,
										OWNER,
										TYPE));
		logger.trace(sb.toString());
		int res = getDb().executeUpdate(sb.toString(), parameterName, parameterValue, owner.id, type);
		logger.debug(MessageFormat.format("{0} notifications supprimées !", res));

		getDb().executeUpdate(MessageFormat.format(	"delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
													NOTIFICATION_ID,
													ID,
													TABLE_NAME));
	}

	/**
	 * Deletes all notification that have the given owner, type and parameters.
	 * 
	 * @param type
	 * @param parameterName
	 * @param parameterValue
	 * @param parameterName2
	 * @param parameterValue2
	 * @throws SQLException
	 */
	public void removeAllType(	NotificationType type,
								ParameterName parameterName,
								Object parameterValue,
								ParameterName parameterName2,
								Object parameterValue2) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("delete from {0} where \n ", TABLE_NAME));
		sb.append(MessageFormat.format(	"    exists (select 1 from {0} p where p.{1} = {2} and p.{3} = ?  and p.{4} = ?) and {5} = ? \n ",
										TABLE_PARAMS,
										NOTIFICATION_ID,
										ID,
										PARAMETER_NAME,
										PARAMETER_VALUE,
										TYPE));
		sb.append(MessageFormat.format(	" and exists (select 1 from {0} p where p.{1} = {2} and p.{3} = ?  and p.{4} = ?) \n ",
										TABLE_PARAMS,
										NOTIFICATION_ID,
										ID,
										PARAMETER_NAME,
										PARAMETER_VALUE));
		logger.trace(sb.toString());
		logger.trace(MessageFormat.format(	"Paramètres: {0} / {1} / {2} / {3} / {4}",
											parameterName,
											parameterValue,
											type,
											parameterName2,
											parameterValue2));
		int nb = getDb().executeUpdate(sb.toString(), parameterName, parameterValue, type, parameterName2, parameterValue2);
		logger.info(MessageFormat.format("Suppression de {0} Notifications.", nb));

		getDb().executeUpdate(MessageFormat.format(	"delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
													NOTIFICATION_ID,
													ID,
													TABLE_NAME));
	}

	/**
	 * 
	 * @param userId
	 * @return The number of notification this user has.
	 */
	public int getUserNotificationCount(int userId) throws SQLException {
		return getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ?", TABLE_NAME, OWNER), userId);
	}

	/**
	 * 
	 * @param userId
	 * @param notif
	 * @return True if and only if the user has already receive this notification.
	 * @throws SQLException
	 */
	public boolean hasNotification(int userId, AbstractNotification notif) throws SQLException {

		Map<ParameterName, Object> parameters = notif.getParameters();

		StringBuilder query = new StringBuilder();
		query.append("select 1 ");
		query.append(MessageFormat.format("from {0} n ", TABLE_NAME));

		Object[] queryParameters = new Object[parameters.keySet().size() * 2 + 2];
		int i = 0;

		for (ParameterName key : parameters.keySet()) {
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
		logger.trace("Parameters => " + parameters);

		return getDb().doesReturnRows(query.toString(), queryParameters);
	}

	/**
	 * Technical method.
	 * 
	 * @param query
	 * @param userId
	 * @param parameters
	 * @return
	 */
	private List<AbstractNotification> getNotificationFromQuery(String query, Object... parameters) {

		PreparedStatementIdKdo ps = null;
		List<AbstractNotification> notifications = new ArrayList<AbstractNotification>();

		try {
			ps = new PreparedStatementIdKdo(getDb(), query);
			ps.bindParameters(parameters);
			if (ps.execute()) {

				ResultSet res = ps.getResultSet();
				int currentId = -1;
				int owner = -1;
				String type = "";
				String text = "";
				Timestamp creation = null;
				boolean isUnread = true;
				Timestamp readOn = null;
				Map<ParameterName, Object> notifParams = new HashMap<ParameterName, Object>();

				while (res.next()) {

					int id = res.getInt(ID.name());
					if (currentId == -1) {
						currentId = id;
						owner = res.getInt(OWNER.name());
						type = res.getString(TYPE.name());
						text = res.getString(TEXT.name());
						creation = res.getTimestamp(CREATION_DATE.name());
						isUnread = "Y".equals(res.getString(IS_UNREAD.name()));
						readOn = res.getTimestamp(READ_ON.name());
					}

					if (id == currentId) {
						// reading parameters
						String name = res.getString(PARAMETER_NAME.name());
						if (name != null && !name.isEmpty()) {
							notifParams.put(ParameterName.valueOf(name), res.getString(PARAMETER_VALUE.name()));
						}
						continue;
					}

					// New id detected, saving the notification
					notifications.add(NotificationFactory.buildIt(	currentId,
																	owner,
																	type,
																	text,
																	creation,
																	isUnread,
																	readOn,
																	notifParams));

					// Initialization for the next notification
					currentId = id;
					owner = res.getInt(OWNER.name());
					type = res.getString(TYPE.name());
					text = res.getString(TEXT.name());
					creation = res.getTimestamp(CREATION_DATE.name());
					isUnread = "Y".equals(res.getString(IS_UNREAD.name()));
					readOn = res.getTimestamp(READ_ON.name());
					notifParams = new HashMap<ParameterName, Object>();
					String name = res.getString(PARAMETER_NAME.name());
					if (name != null && !name.isEmpty()) {
						notifParams.put(ParameterName.valueOf(name), res.getString(PARAMETER_VALUE.name()));
					}
				}

				if (currentId != -1) {
					notifications.add(NotificationFactory.buildIt(	currentId,
																	owner,
																	type,
																	text,
																	creation,
																	isUnread,
																	readOn,
																	notifParams));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return notifications;
	}

	/**
	 * 
	 * @param whereClause
	 * @param parameters
	 * 
	 * @return The notifications matched by this where clause or all.
	 */
	private List<AbstractNotification> getNotificationWithWhereClause(String whereClause, Object... parameters) {

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8} ",
											ID,
											TEXT,
											TYPE,
											OWNER,
											PARAMETER_NAME,
											PARAMETER_VALUE,
											CREATION_DATE,
											IS_UNREAD,
											READ_ON));
		query.append(MessageFormat.format("  from {0} ", TABLE_NAME));
		query.append(MessageFormat.format("  left join {0} ", TABLE_PARAMS));
		query.append(MessageFormat.format("    on {0} = {1} ", ID, NOTIFICATION_ID));

		if (whereClause != null && !whereClause.isEmpty()) {
			query.append(MessageFormat.format(" where {0}", whereClause));
		}
		query.append(MessageFormat.format(" order by {0} desc", NOTIFICATION_ID));
		logger.trace(MessageFormat.format("Query: {0}", query.toString()));
		logger.trace(MessageFormat.format("Parameters: {0}", Arrays.toString(parameters)));

		return getNotificationFromQuery(query.toString(), parameters);
	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<AbstractNotification> getUserNotifications(int userId) throws SQLException {
		return getNotificationWithWhereClause(MessageFormat.format("{0} = ?", OWNER), userId);
	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<AbstractNotification> getUserNotifications(int userId, NotificationType type) throws SQLException {
		return getNotificationWithWhereClause(MessageFormat.format("{0} = ? and {1} = ?", OWNER, TYPE), userId, type.name());
	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<AbstractNotification> getUserReadNotifications(int userId) throws SQLException {
		return getNotificationWithWhereClause(MessageFormat.format("{0} = ? and {1} = ?", OWNER, IS_UNREAD), userId, "N");
	}

	/**
	 * 
	 * @param userId
	 * @return All notifications for this user.
	 * @throws SQLException
	 */
	public List<AbstractNotification> getUserUnReadNotifications(int userId) throws SQLException {
		return getNotificationWithWhereClause(MessageFormat.format("{0} = ? and {1} = ?", OWNER, IS_UNREAD), userId, "Y");
	}

	/**
	 * 
	 * @param owner
	 * @param type
	 * @param parameterName
	 * @param parameterValue
	 * @return The list of notification having the given parameter name equals to this value.
	 */
	public List<AbstractNotification> getNotifications(	int owner,
														NotificationType type,
														ParameterName parameterName,
														Object parameterValue) {
		String whereClause = MessageFormat.format(	" exists (select 1 from {0} where {1} = {2} and {3} = ?  and {4} = ?) and {5} = ? and {6} = ?",
													TABLE_PARAMS,
													NOTIFICATION_ID,
													ID,
													PARAMETER_NAME,
													PARAMETER_VALUE,
													OWNER,
													TYPE);
		return getNotificationWithWhereClause(whereClause, parameterName, parameterValue, owner, type);
	}

	/**
	 * 
	 * @param parameterName
	 * @param parameterValue
	 * @return The list of notification having the given parameter name equals to this value.
	 * @throws SQLException
	 */
	public List<AbstractNotification> getNotification(ParameterName parameterName, Object parameterValue) throws SQLException {
		String whereClause = MessageFormat.format(	" exists (select 1 from {0} where {1} = {2} and {3} = ?  and {4} = ?)",
													TABLE_PARAMS,
													NOTIFICATION_ID,
													ID,
													PARAMETER_NAME,
													PARAMETER_VALUE);
		return getNotificationWithWhereClause(whereClause, parameterName, parameterValue);
	}

	/**
	 * 
	 * @param notifId
	 * @return The notification corresponding to this id.
	 * @throws SQLException
	 */
	public AbstractNotification getNotification(int notifId) throws SQLException {
		List<AbstractNotification> notifs = getNotificationWithWhereClause(MessageFormat.format("{0} = ?", ID), notifId);
		return notifs.size() == 0 ? null : notifs.get(0);
	}

	/**
	 * Manually sends a new notification to the ADMIN.
	 * 
	 * @param type
	 * @param message
	 */
	private void sendAdminNotification(String type, String message) {

		// Insert a DB notification
		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(" insert into {0} ", TABLE_NAME));
		query.append(MessageFormat.format("   ({0},{1},{2},{3}) ", OWNER, TEXT, TYPE, CREATION_DATE));
		query.append(MessageFormat.format(	" select u.{0}, ''{1}'', ''{2}'', now()",
		                                  	UsersColumns.ID,
		                                  	message,
		                                  	type));
		query.append(MessageFormat.format("   from {0} u ", Users.TABLE_NAME));
		query.append(MessageFormat.format("   join USER_ROLES ur on ur.{0} = u.{1}", UserRolesColumns.EMAIL, UsersColumns.EMAIL));
		query.append(MessageFormat.format("  where ur.{0} = ?", UserRolesColumns.ROLE));
		
		getDb().executeUpdate(query.toString(), "ROLE_ADMIN");
		
		// Send emails to the ADMIN
		query = new StringBuilder();
		query.append(MessageFormat.format("select u.{0} ", UsersColumns.EMAIL));
		query.append(MessageFormat.format("  from {0} u ", Users.TABLE_NAME));
		query.append(MessageFormat.format("  join USER_ROLES ur on ur.{0} = u.{1} ", UserRolesColumns.EMAIL, UsersColumns.EMAIL));
		query.append(MessageFormat.format("  where ur.{0} = ?", UserRolesColumns.ROLE));
		
		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(getDb(), query.toString());
			ps.bindParameters("ROLE_ADMIN");
			ps.execute();
			ResultSet res = ps.getResultSet();
			
			while (res.next()) {
				String emailAdress = res.getString(UsersColumns.EMAIL.name());
				try {
					InputStream input = getClass().getResourceAsStream("/notif.properties");
					Properties p = new Properties();
					try {
						p.load(new InputStreamReader(input, "UTF-8"));
					} catch (IOException e) {
						e.printStackTrace();
						logger.warn(e);
					}
					
					String messageTemplate = p.get("mail_template").toString();
					String body = messageTemplate.replaceAll("\\$\\$text\\$\\$", Matcher.quoteReplacement(message));
					EmailSender.sendEmail(emailAdress, "Nos idées de cadeaux - Admin notification...", body);
					
				} catch (Exception e) {
					logger.warn(MessageFormat.format("Fail to send error email to {0}", emailAdress));
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * Send a notification to the admin that a new person has signed in.
	 * 
	 * @param message
	 */
	public void notifyAboutANewInscription(String message) {
		logger.info(MessageFormat.format("New person nofication ! Message: {0}", message));
		sendAdminNotification(NOTIF_TYPE_NEW_INSCRIPTION, message);
	}

	/**
	 * Send a notification to the admin that an error has occurred.
	 * 
	 * @param message
	 */
	public void notifyAboutAnError(String message) {
		sendAdminNotification(NOTIF_TYPE_ADMIN_ERROR, message);
	}

	/**
	 * Logs an exception notifications to Admins.
	 * 
	 * @param thisOne The current connected user.
	 * @param exception
	 * @param req
	 * @throws SQLException
	 */
	public void logError(User thisOne, Exception exception, HttpServletRequest req) {

		StringBuilder notifText = new StringBuilder();
		notifText.append("L'erreur suivante est survenue: " + exception.getMessage() + "<br/>");
		if (thisOne != null) {
			notifText.append("Email de l'utilisateur connecté: " + thisOne.getEmail() + "<br/>");
		} else {
			notifText.append("Aucun utilisateur connecté.<br/>");
		}

		notifText.append("Stacktrace:<br/><br/>");
		for (StackTraceElement line : exception.getStackTrace()) {
			notifText.append(line);
			notifText.append("<br/>");
		}

		notifyAboutAnError(notifText.toString());
	}

	/**
	 * Set the notification as read.
	 * 
	 * @param notifId
	 * @throws SQLException
	 */
	public void setRead(int notifId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format("update {0} set {1} = ?, {2} = now() where {3} = ? ",
													TABLE_NAME,
													IS_UNREAD,
													READ_ON,
													ID),
								"N",
								notifId);
	}

	/**
	 * Set the notification as unread.
	 * 
	 * @param notifId
	 * @throws SQLException
	 */
	public void setUnread(int notifId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ? ", TABLE_NAME, IS_UNREAD, ID), "Y", notifId);
	}

	public void removeAll(int userId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ", TABLE_NAME, OWNER), userId);
		String query = MessageFormat.format("delete from {0} where not exists (select 1 from {1} n where {2} = n.{3}) ",
											TABLE_PARAMS,
											TABLE_NAME,
											NOTIFICATION_ID,
											ID);
		logger.trace(query);
		getDb().executeUpdate(query);
	}
}