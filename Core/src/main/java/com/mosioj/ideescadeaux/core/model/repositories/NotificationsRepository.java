package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdoInserter;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.*;
import com.mosioj.ideescadeaux.core.model.repositories.columns.NotificationsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.EmailSender;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.mosioj.ideescadeaux.core.model.repositories.columns.NotificationParametersColumns.*;

public class NotificationsRepository extends AbstractRepository {

    private static final Logger logger = LogManager.getLogger(NotificationsRepository.class);

    public static final String TABLE_NAME = "NOTIFICATIONS";
    public static final String TABLE_PARAMS = "NOTIFICATION_PARAMETERS";

    public static final String NOTIF_TYPE_ADMIN_ERROR = "ADMIN_ERROR";
    public static final String NOTIF_TYPE_NEW_INSCRIPTION = "NEW_INSCRIPTION";

    private static final Properties notificationProperties = new Properties();

    private NotificationsRepository() {
        // Forbidden
    }

    /**
     * Save and send a notification.
     *
     * @param userId The user that will receive this notification.
     * @param notif  The notification.
     * @return The id of the notification created, or -1 if none were created.
     */
    public static int addNotification(int userId, AbstractNotification notif) {

        logger.info(MessageFormat.format("Creating notification {0} for user {1}", notif.getType(), userId));
        int id = -1;
        try {
            NotificationActivation activation = getActivationType(userId, notif);

            // Insertion en base
            if (activation == NotificationActivation.SITE || activation == NotificationActivation.EMAIL_SITE) {
                try (PreparedStatementIdKdoInserter ps = new PreparedStatementIdKdoInserter(getDb(),
                                                                                            MessageFormat.format(
                                                                                                    "insert into {0} (owner, text, type, creation_date) values (?, ?, ?, now())",
                                                                                                    TABLE_NAME))) {
                    ps.bindParameters(userId, notif.getTextToInsert(), notif.getType());
                    id = ps.executeUpdate();
                }

                if (id > 0) {
                    logger.debug(MessageFormat.format("Creating parameters for notification {0}", id));
                    Map<ParameterName, Object> notifParams = notif.getParameters();
                    for (ParameterName key : notifParams.keySet()) {
                        try (PreparedStatementIdKdoInserter ps = new PreparedStatementIdKdoInserter(getDb(),
                                                                                                    MessageFormat.format(
                                                                                                            "insert into {0} ({1},{2},{3}) values (?, ?, ?)",
                                                                                                            TABLE_PARAMS,
                                                                                                            NOTIFICATION_ID,
                                                                                                            PARAMETER_NAME,
                                                                                                            PARAMETER_VALUE))) {
                            ps.bindParameters(id, key, notifParams.get(key).toString());
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Envoie de la notification par email si besoin
            if (activation == NotificationActivation.EMAIL || activation == NotificationActivation.EMAIL_SITE) {
                getDb().selectString(MessageFormat.format("select {0} from {1} where {2} = ?",
                                                          UsersColumns.EMAIL,
                                                          UsersRepository.TABLE_NAME,
                                                          UsersColumns.ID),
                                     userId)
                       .ifPresent(e -> notif.sendEmail(e, notificationProperties.get("urlTillProtectedPublic")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Exception while trying to add a notification: " + e.getMessage());
        }

        return id;
    }

    /**
     * @param userId The user id.
     * @param notif  The notification.
     * @return The activation type. Default is EMAIL_SITE.
     */
    private static NotificationActivation getActivationType(int userId,
                                                            AbstractNotification notif) throws SQLException {
        try {
            final String value = UserParametersRepository.getParameter(userId, notif.getType()).orElse("EMAIL_SITE");
            return NotificationActivation.valueOf(value);
        } catch (IllegalArgumentException e) {
            return NotificationActivation.EMAIL_SITE;
        }
    }

    /**
     * @param user      The user.
     * @param notifType The notification type.
     */
    public static void removeAllType(User user, NotificationType notifType) {
        logger.info(MessageFormat.format("Delete notification {0} for user {1}", notifType.name(), user));
        try {
            getDb().executeUpdate(MessageFormat.format("delete from {0} where owner = ? and type = ?", TABLE_NAME),
                                  user.id,
                                  notifType.name());
            getDb().executeUpdate(MessageFormat.format(
                    "delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
                    NOTIFICATION_ID,
                    NotificationsColumns.ID,
                    TABLE_NAME));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Impossible de supprimer les notifications..." + e.getMessage());
        }
    }

    public static void remove(AbstractNotification notification) {
        try {
            logger.info(MessageFormat.format("Suppression de la notification {0}", notification.id));
            getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                       TABLE_NAME,
                                                       NotificationsColumns.ID), notification.id);
            getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                       TABLE_PARAMS,
                                                       NOTIFICATION_ID),
                                  notification.id);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * Deletes all notification that have the given owner, type and parameters.
     *
     * @param owner          The owner.
     * @param type           The type.
     * @param parameterName  The parameter name.
     * @param parameterValue The parameter value.
     */
    public static void removeAllType(User owner,
                                     NotificationType type,
                                     ParameterName parameterName,
                                     Object parameterValue) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(MessageFormat.format("delete from {0} where ", TABLE_NAME));
            sb.append(MessageFormat.format(
                    " exists (select 1 from {0} p where p.{1} = {2} and p.{3} = ?  and p.{4} = ?) and {5} = ? and {6} = ?",
                    TABLE_PARAMS,
                    NOTIFICATION_ID,
                    NotificationsColumns.ID,
                    PARAMETER_NAME,
                    PARAMETER_VALUE,
                    NotificationsColumns.OWNER,
                    NotificationsColumns.TYPE));
            logger.trace(sb.toString());
            int res = getDb().executeUpdate(sb.toString(), parameterName, parameterValue, owner.id, type);
            if (res > 0) {
                logger.debug(MessageFormat.format("{0} notifications supprimées !", res));
            }

            getDb().executeUpdate(MessageFormat.format(
                    "delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
                    NOTIFICATION_ID,
                    NotificationsColumns.ID,
                    TABLE_NAME));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    /**
     * Deletes all notification that have the given owner, type and parameters.
     *
     * @param type            The notification type.
     * @param parameterName   The parameter name.
     * @param parameterValue  The parameter value.
     * @param parameterName2  Another parameter name.
     * @param parameterValue2 Another parameter value.
     */
    public static void removeAllType(NotificationType type,
                                     ParameterName parameterName,
                                     Object parameterValue,
                                     ParameterName parameterName2,
                                     Object parameterValue2) throws SQLException {

        final String query = "DELETE FROM " + TABLE_NAME +
                             " WHERE " + NotificationsColumns.ID + " IN ( " +
                             "    SELECT p1." + NOTIFICATION_ID +
                             "      FROM " + TABLE_PARAMS + " p1 " +
                             "      LEFT JOIN " + TABLE_PARAMS + " p2 " +
                             "        ON p1." + NOTIFICATION_ID + " = p2." + NOTIFICATION_ID +
                             "       AND p2." + PARAMETER_NAME + " = ? " +
                             "       AND p2." + PARAMETER_VALUE + " = ? " +
                             "     WHERE p1." + PARAMETER_NAME + " = ? " +
                             "       AND p1." + PARAMETER_VALUE + " = ? " +
                             "       AND p1.id_param IS NOT NULL " +
                             "       AND p2.id_param IS NOT NULL) " +
                             "   AND TYPE = ?";

        logger.trace("[Perf] Executing query: {}", query);
        logger.trace(MessageFormat.format("Paramètres: {0} / {1} / {2} / {3} / {4}",
                                          parameterName,
                                          parameterValue,
                                          parameterName2,
                                          parameterValue2,
                                          type));
        int nb = getDb().executeUpdate(query,
                                       parameterName,
                                       parameterValue,
                                       parameterName2,
                                       parameterValue2,
                                       type);
        logger.info(MessageFormat.format("Suppression de {0} Notifications.", nb));

        getDb().executeUpdate(MessageFormat.format(
                "delete from NOTIFICATION_PARAMETERS where {0} not in (select {1} from {2})",
                NOTIFICATION_ID,
                NotificationsColumns.ID,
                TABLE_NAME));
    }

    /**
     * @param userId The user id.
     * @return The number of notification this user has.
     */
    public static int getUserNotificationCount(int userId) {
        return getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                            TABLE_NAME,
                                                            NotificationsColumns.OWNER), userId);
    }

    /**
     * @param userId The user id.
     * @param notif  The notification.
     * @return True if and only if the user has already receive this notification.
     */
    public static List<AbstractNotification> findNotificationMatching(int userId, AbstractNotification notif) {
        try {
            List<AbstractNotification> found = getNotificationWithWhereClause(MessageFormat.format("{0} = ? and {1} = ?",
                                                                                                   NotificationsColumns.TYPE,
                                                                                                   NotificationsColumns.OWNER),
                                                                              notif.getType(),
                                                                              userId);

            // les constructions de paramètres depuis la base sont forcément en <ParameterName, String>
            final Map<ParameterName, Object> parameters = notif.getParameters();
            final Map<ParameterName, String> typedParameters = new HashMap<>();
            parameters.forEach((key, value) -> typedParameters.put(key, value == null ? null : value.toString()));

            // On ne conserve que ceux qui matchent les paramètres
            return found.stream().filter(n -> n.getParameters().equals(typedParameters)).collect(Collectors.toList());

        } catch (SQLException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }

    /**
     * Technical method.
     *
     * @param query      The query.
     * @param parameters The parameters.
     * @return The list of notification found.
     */
    private static List<AbstractNotification> getNotificationFromQuery(String query,
                                                                       Object... parameters) throws SQLException {

        List<AbstractNotification> notifications = new ArrayList<>();
        logger.trace("[Perf] getNotificationFromQuery. Query: {}. Parameters: {}", query, Arrays.toString(parameters));

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(parameters);
            if (ps.execute()) {

                logger.trace("[Perf] Execution completed! Building the result...");
                ResultSet res = ps.getResultSet();
                int currentId = -1;
                User owner = null;
                String type = "";
                String text = "";
                Timestamp creation = null;
                boolean isUnread = true;
                Timestamp readOn = null;
                Map<ParameterName, Object> notifParams = new HashMap<>();

                while (res.next()) {

                    int id = res.getInt(NotificationsColumns.ID.name());
                    if (currentId == -1) {
                        currentId = id;
                        owner = new User(res.getInt("user_id"),
                                         res.getString(UsersColumns.NAME.name()),
                                         res.getString(UsersColumns.EMAIL.name()),
                                         res.getDate(UsersColumns.BIRTHDAY.name()),
                                         res.getString(UsersColumns.AVATAR.name()));
                        type = res.getString(NotificationsColumns.TYPE.name());
                        text = res.getString(NotificationsColumns.TEXT.name());
                        creation = res.getTimestamp(NotificationsColumns.CREATION_DATE.name());
                        isUnread = "Y".equals(res.getString(NotificationsColumns.IS_UNREAD.name()));
                        readOn = res.getTimestamp(NotificationsColumns.READ_ON.name());
                    }

                    if (id == currentId) {
                        // reading parameters
                        String name = res.getString(PARAMETER_NAME.name());
                        if (name != null && !name.isEmpty()) {
                            notifParams.put(ParameterName.valueOf(name),
                                            res.getString(PARAMETER_VALUE.name()));
                        }
                        continue;
                    }

                    // New id detected, saving the notification
                    notifications.add(NotificationFactory.buildIt(currentId,
                                                                  owner,
                                                                  type,
                                                                  text,
                                                                  creation,
                                                                  isUnread,
                                                                  readOn,
                                                                  notifParams));

                    // Initialization for the next notification
                    currentId = id;
                    owner = new User(res.getInt("user_id"),
                                     res.getString(UsersColumns.NAME.name()),
                                     res.getString(UsersColumns.EMAIL.name()),
                                     res.getDate(UsersColumns.BIRTHDAY.name()),
                                     res.getString(UsersColumns.AVATAR.name()));
                    type = res.getString(NotificationsColumns.TYPE.name());
                    text = res.getString(NotificationsColumns.TEXT.name());
                    creation = res.getTimestamp(NotificationsColumns.CREATION_DATE.name());
                    isUnread = "Y".equals(res.getString(NotificationsColumns.IS_UNREAD.name()));
                    readOn = res.getTimestamp(NotificationsColumns.READ_ON.name());
                    notifParams = new HashMap<>();
                    String name = res.getString(PARAMETER_NAME.name());
                    if (name != null && !name.isEmpty()) {
                        notifParams.put(ParameterName.valueOf(name),
                                        res.getString(PARAMETER_VALUE.name()));
                    }
                }

                if (currentId != -1) {
                    notifications.add(NotificationFactory.buildIt(currentId,
                                                                  owner,
                                                                  type,
                                                                  text,
                                                                  creation,
                                                                  isUnread,
                                                                  readOn,
                                                                  notifParams));
                }

            }
        }

        logger.trace("[Perf] Completed! Created {} objects.", notifications.size());
        return notifications;
    }

    /**
     * @param whereClause The where clause.
     * @param parameters  The parameters.
     * @return The notifications matched by this where clause or all.
     */
    private static List<AbstractNotification> getNotificationWithWhereClause(String whereClause,
                                                                             Object... parameters) throws SQLException {

        String query = "select  n." + NotificationsColumns.ID + "," +
                       "        n." + NotificationsColumns.TEXT + "," +
                       "        n." + NotificationsColumns.TYPE + "," +
                       "        u." + UsersColumns.ID + " as user_id," +
                       "        u." + UsersColumns.NAME + "," +
                       "        u." + UsersColumns.EMAIL + "," +
                       "        u." + UsersColumns.BIRTHDAY + "," +
                       "        u." + UsersColumns.AVATAR + "," +
                       "       np." + PARAMETER_NAME + "," +
                       "       np." + PARAMETER_VALUE + "," +
                       "        n." + NotificationsColumns.CREATION_DATE + "," +
                       "        n." + NotificationsColumns.IS_UNREAD + "," +
                       "        n." + NotificationsColumns.READ_ON +
                       "  from " + TABLE_NAME + " n " +
                       "  left join " + TABLE_PARAMS + " np " +
                       "    on " + NotificationsColumns.ID + " = " + NOTIFICATION_ID +
                       "  left join " + UsersRepository.TABLE_NAME + " u " +
                       "    on u." + UsersColumns.ID + " = " + NotificationsColumns.OWNER;

        if (!StringUtils.isBlank(whereClause)) {
            query += " where " + whereClause;
        }

        query += " order by n." + NotificationsColumns.ID + " desc";

        logger.info(MessageFormat.format("Query: {0}", query));
        logger.trace(MessageFormat.format("Parameters: {0}", Arrays.toString(parameters)));

        return getNotificationFromQuery(query, parameters);
    }

    /**
     * @param user The user.
     * @return All notifications for this user.
     */
    public static List<AbstractNotification> getUserNotifications(User user) {
        try {
            return getNotificationWithWhereClause(MessageFormat.format("n.{0} = ?", NotificationsColumns.OWNER),
                                                  user.id);
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }

    /**
     * @param userId The user id.
     * @return All notifications for this user.
     */
    public static List<AbstractNotification> getUserNotifications(int userId,
                                                                  NotificationType type) throws SQLException {
        return getNotificationWithWhereClause(MessageFormat.format("n.{0} = ? and n.{1} = ?",
                                                                   NotificationsColumns.OWNER,
                                                                   NotificationsColumns.TYPE),
                                              userId,
                                              type.name());
    }

    /**
     * @param userId The user id.
     * @return All notifications for this user.
     */
    public static List<AbstractNotification> getUserReadNotifications(int userId) throws SQLException {
        return getNotificationWithWhereClause(MessageFormat.format("n.{0} = ? and n.{1} = ?",
                                                                   NotificationsColumns.OWNER,
                                                                   NotificationsColumns.IS_UNREAD),
                                              userId,
                                              "N");
    }

    /**
     * @param userId The user id.
     * @return All notifications for this user.
     */
    public static List<AbstractNotification> getUserUnReadNotifications(int userId) throws SQLException {
        return getNotificationWithWhereClause(MessageFormat.format("n.{0} = ? and n.{1} = ?",
                                                                   NotificationsColumns.OWNER,
                                                                   NotificationsColumns.IS_UNREAD),
                                              userId,
                                              "Y");
    }

    /**
     * @param owner          The owner.
     * @param type           The notification type.
     * @param parameterName  The parameter name.
     * @param parameterValue The paramter value.
     * @return The list of notification having the given parameter name equals to this value.
     */
    public static List<AbstractNotification> getNotifications(int owner,
                                                              NotificationType type,
                                                              ParameterName parameterName,
                                                              Object parameterValue) throws SQLException {
        String whereClause = MessageFormat.format(
                " exists (select 1 from {0} where {1} = n.{2} and {3} = ?  and {4} = ?) and n.{5} = ? and n.{6} = ?",
                TABLE_PARAMS,
                NOTIFICATION_ID,
                NotificationsColumns.ID,
                PARAMETER_NAME,
                PARAMETER_VALUE,
                NotificationsColumns.OWNER,
                NotificationsColumns.TYPE);
        return getNotificationWithWhereClause(whereClause, parameterName, parameterValue, owner, type);
    }

    /**
     * @param parameterName  The parameter name.
     * @param parameterValue The paramter value.
     * @return The list of notification having the given parameter name equals to this value.
     */
    public static List<AbstractNotification> getNotification(ParameterName parameterName,
                                                             Object parameterValue) throws SQLException {
        String whereClause = MessageFormat.format(" exists (select 1 from {0} where {1} = n.{2} and {3} = ?  and {4} = ?)",
                                                  TABLE_PARAMS,
                                                  NOTIFICATION_ID,
                                                  NotificationsColumns.ID,
                                                  PARAMETER_NAME,
                                                  PARAMETER_VALUE);
        return getNotificationWithWhereClause(whereClause, parameterName, parameterValue);
    }

    /**
     * @param notifId The notification id.
     * @return The notification corresponding to this id.
     */
    public static Optional<AbstractNotification> getNotification(int notifId) {
        try {
            final String whereClause = MessageFormat.format("n.{0} = ?", NotificationsColumns.ID);
            return getNotificationWithWhereClause(whereClause, notifId).stream().findFirst();
        } catch (SQLException e) {
            logger.warn(e);
            return Optional.empty();
        }
    }

    /**
     * Manually sends a new notification to the ADMIN.
     *
     * @param type    The notification type.
     * @param message The message.
     */
    private static void sendAdminNotification(String type, String message) {

        // Insert a DB notification
        logger.info("Envoie d'une notification admin de type {}...", type);

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format(" insert into {0} ", TABLE_NAME));
        query.append(MessageFormat.format("   ({0},{1},{2},{3}) ",
                                          NotificationsColumns.OWNER,
                                          NotificationsColumns.TEXT,
                                          NotificationsColumns.TYPE,
                                          NotificationsColumns.CREATION_DATE));
        query.append(MessageFormat.format(" select u.{0}, ?, ?, now()",
                                          UsersColumns.ID));
        query.append(MessageFormat.format("   from {0} u ", UsersRepository.TABLE_NAME));
        query.append(MessageFormat.format("   join USER_ROLES ur on ur.{0} = u.{1}",
                                          UserRolesColumns.EMAIL,
                                          UsersColumns.EMAIL));
        query.append(MessageFormat.format("  where ur.{0} = ?", UserRolesColumns.ROLE));

        try {
            getDb().executeUpdate(query.toString(), message, type, "ROLE_ADMIN");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Query is => {}.", query);
            logger.warn("Fail to write notification.", e);
        }

        // Send emails to the ADMIN
        query = new StringBuilder();
        query.append(MessageFormat.format("select u.{0} ", UsersColumns.EMAIL));
        query.append(MessageFormat.format("  from {0} u ", UsersRepository.TABLE_NAME));
        query.append(MessageFormat.format("  join USER_ROLES ur on ur.{0} = u.{1} ",
                                          UserRolesColumns.EMAIL,
                                          UsersColumns.EMAIL));
        query.append(MessageFormat.format("  where ur.{0} = ?", UserRolesColumns.ROLE));

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters("ROLE_ADMIN");
            ps.execute();
            ResultSet res = ps.getResultSet();

            while (res.next()) {
                String emailAdress = res.getString(UsersColumns.EMAIL.name());
                String messageTemplate = notificationProperties.get("mail_template").toString();
                String body = messageTemplate.replaceAll("\\$\\$text\\$\\$", Matcher.quoteReplacement(message));
                EmailSender.sendEmail(emailAdress, "Nos idées de cadeaux - Admin notification...", body);
            }
        } catch (SQLException e) {
            logger.warn("Fail to write notification.", e);
        }
    }

    /**
     * Send a notification to the admin that a new person has signed in.
     *
     * @param message The message.
     */
    public static void notifyAboutANewInscription(String message) {
        logger.info(MessageFormat.format("New person nofication ! Message: {0}", message));
        sendAdminNotification(NOTIF_TYPE_NEW_INSCRIPTION, message);
    }

    /**
     * Send a notification to the admin that an error has occurred.
     *
     * @param message The message.
     */
    public static void notifyAboutAnError(String message) {
        sendAdminNotification(NOTIF_TYPE_ADMIN_ERROR, message);
    }

    /**
     * Logs an exception notifications to Admins.
     *
     * @param thisOne   The current connected user.
     * @param exception The exception thrown.
     */
    public static void logError(User thisOne, Exception exception) {

        StringBuilder notifText = new StringBuilder();
        notifText.append("L'erreur suivante est survenue: ").append(exception.getMessage()).append("<br/>");
        if (thisOne != null) {
            notifText.append("Email de l'utilisateur connecté: ").append(thisOne.getEmail()).append("<br/>");
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
     * @param notif The notification.
     */
    public static void setRead(AbstractNotification notif) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ?, {2} = now() where {3} = ? ",
                                                   TABLE_NAME,
                                                   NotificationsColumns.IS_UNREAD,
                                                   NotificationsColumns.READ_ON,
                                                   NotificationsColumns.ID),
                              "N",
                              notif.id);
    }

    /**
     * Set the notification as unread.
     *
     * @param notif The notification.
     */
    public static void setUnread(AbstractNotification notif) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ? ",
                                                   TABLE_NAME,
                                                   NotificationsColumns.IS_UNREAD,
                                                   NotificationsColumns.ID),
                              "Y",
                              notif.id);
    }

    public static void removeAll(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? ",
                                                   TABLE_NAME,
                                                   NotificationsColumns.OWNER), userId);
        String query = MessageFormat.format("delete from {0} where not exists (select 1 from {1} n where {2} = n.{3}) ",
                                            TABLE_PARAMS,
                                            TABLE_NAME,
                                            NOTIFICATION_ID,
                                            NotificationsColumns.ID);
        logger.trace(query);
        getDb().executeUpdate(query);
    }

    static {
        InputStream input = NotificationsRepository.class.getResourceAsStream("/notif.properties");
        try {
            notificationProperties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e);
        }
    }
}
