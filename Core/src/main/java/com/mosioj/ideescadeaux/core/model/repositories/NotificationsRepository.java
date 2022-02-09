package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdoInserter;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationFactory;
import com.mosioj.ideescadeaux.core.model.repositories.columns.NotificationsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.EmailSender;
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
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.mosioj.ideescadeaux.core.model.repositories.columns.NotificationsColumns.*;

public class NotificationsRepository extends AbstractRepository {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(NotificationsRepository.class);

    /** The table name storing notifications. */
    public static final String TABLE_NAME = "NOTIFICATIONS";

    /** Specific type for administration notification. */
    public static final String NOTIF_TYPE_ADMIN_ERROR = "ADMIN_ERROR";

    /** Specific type for administration notification. */
    public static final String NOTIF_TYPE_NEW_INSCRIPTION = "NEW_INSCRIPTION";

    /** The properties used by this repository. */
    private static final Properties notificationProperties = new Properties();

    /**
     * Send the notification by email.
     *
     * @param notification The notification.
     */
    public static void sendEmail(Notification notification) {
        final String start = "<a href=\"" + notificationProperties.get("urlTillProtectedPublic");
        String text = notification.getText()
                                  .replaceAll("<a href=\"protected/", start + "protected/")
                                  .replaceAll("<a href=\"public/", start + "public/");
        String body = notificationProperties.get("mail_template").toString().replaceAll("\\$\\$text\\$\\$", text);
        EmailSender.sendEmail(notification.getOwner().getEmail(),
                              "Nos idées de cadeaux - Nouvelle notification !",
                              body);
    }

    /**
     * Save and send a notification.
     *
     * @param notification The notification.
     * @return The id of the notification created, or -1 if none were created.
     */
    // TODO retourner un optional de Integer voire de Notification
    public static int add(Notification notification) {

        logger.info("Creating notification {} for user {}. Parameters: user={}, idea={}, group={}.",
                    notification.getType(),
                    notification.getOwner(),
                    notification.getUserParameter(),
                    notification.getIdeaParameter(),
                    notification.getGroupParameter());
        int id = -1;
        try {
            NotificationActivation activation = getActivationType(notification.getOwner().getId(), notification);

            // Insertion en base
            if (activation == NotificationActivation.SITE || activation == NotificationActivation.EMAIL_SITE) {
                final String query = "insert into NOTIFICATIONS " +
                                     " (owner, type, creation_date, user_id_param, idea_id_param, group_id_param) " +
                                     " values " +
                                     " (?,     ?,    now(),         ?,             ?,             ?)";
                try (PreparedStatementIdKdoInserter ps = new PreparedStatementIdKdoInserter(getDb(), query)) {
                    ps.bindParameters(notification.getOwner().getId(),
                                      notification.getType(),
                                      notification.getUserParameter().orElse(null),
                                      notification.getIdeaParameter().orElse(null),
                                      notification.getGroupParameter().orElse(null));
                    id = ps.executeUpdate();
                }
            }

            // Envoie de la notification par email si besoin
            if (activation == NotificationActivation.EMAIL || activation == NotificationActivation.EMAIL_SITE) {
                sendEmail(notification);
            }
        } catch (SQLException e) {
            logger.warn("Exception while trying to add a notification.", e);
        }

        return id;
    }

    /**
     * @param userId The user id.
     * @param notif  The notification.
     * @return The activation type. Default is EMAIL_SITE.
     */
    private static NotificationActivation getActivationType(int userId,
                                                            Notification notif) throws SQLException {
        try {
            final String value = UserParametersRepository.getParameter(userId, notif.getType().name())
                                                         .orElse("EMAIL_SITE");
            return NotificationActivation.valueOf(value);
        } catch (IllegalArgumentException e) {
            return NotificationActivation.EMAIL_SITE;
        }
    }

    // -----------------------------------------------------------------------
    // --------------------          DROP METHOD          --------------------
    // -----------------------------------------------------------------------

    /**
     * Delete the corresponding notification if found, based on the identifier.
     *
     * @param notification The notification to delete.
     */
    public static void remove(Notification notification) {
        logger.info("Suppression de la notification {}", notification.id);
        terminator().whereId(notification.getId()).terminates();
    }

    /**
     * @return A new terminator builder.
     */
    public static NotificationTerminator terminator() {
        return new NotificationTerminator();
    }

    public static class NotificationTerminator {

        /** The internal query. */
        StringBuilder query = new StringBuilder("delete from ").append(TABLE_NAME).append(" where 1 = 1 ");

        /** Parameters list */
        List<Object> parameters = new ArrayList<>();

        /**
         * Adds an identifier filter.
         *
         * @param id The notification identifier.
         * @return The builder instance.
         */
        public NotificationTerminator whereId(Long id) {
            query.append(" and id = ?");
            parameters.add(id);
            return this;
        }

        /**
         * Adds a filter on the notification's owner.
         *
         * @param owner The notification owner.
         * @return The builder instance.
         */
        public NotificationTerminator whereOwner(User owner) {
            query.append(" and owner = ? ");
            parameters.add(owner.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's type.
         *
         * @param types The notification types.
         * @return The builder instance.
         */
        public NotificationTerminator whereType(NType... types) {
            List<NType> typesList = Arrays.asList(types);
            query.append(" and type in (")
                 .append(typesList.stream().map(t -> "?").collect(Collectors.joining(",")))
                 .append(")");
            parameters.addAll(typesList);
            return this;
        }

        /**
         * Adds a filter on the notification's user parameter.
         *
         * @param user The notification's user parameter.
         * @return The builder instance.
         */
        public NotificationTerminator whereUser(User user) {
            query.append(" and user_id_param = ? ");
            parameters.add(user.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's idea parameter.
         *
         * @param idea The notification's idea parameter.
         * @return The builder instance.
         */
        public NotificationTerminator whereIdea(Idee idea) {
            query.append(" and idea_id_param = ? ");
            parameters.add(idea.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's group idea parameter.
         *
         * @param group The notification's group idea parameter.
         * @return The builder instance.
         */
        public NotificationTerminator whereGroupIdea(IdeaGroup group) {
            query.append(" and group_id_param = ? ");
            parameters.add(group.getId());
            return this;
        }

        /**
         * Runs the query built so far.
         */
        public void terminates() {
            try {
                int nb = getDb().executeUpdate(query.toString(), parameters.toArray());
                logger.debug("{} notifications deleted using {}, and {}.", nb, parameters, query);
            } catch (SQLException e) {
                logger.warn("Fail to execute {} with the parameters {}. Got: {}.", query, parameters, e);
            }
        }
    }

    // -------------------------------------------------------------------
    // --------------------          FETCHER          --------------------
    // -------------------------------------------------------------------

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
     * @return A new fetcher builder.
     */
    public static NotificationFetcher fetcher() {
        return new NotificationFetcher();
    }

    public static class NotificationFetcher {

        final String query = "select  n." + NotificationsColumns.ID + "," +
                             "        n." + NotificationsColumns.TYPE + "," +
                             "        u." + UsersColumns.ID + " as user_id," +
                             "        u." + UsersColumns.NAME + "," +
                             "        u." + UsersColumns.EMAIL + "," +
                             "        u." + UsersColumns.BIRTHDAY + "," +
                             "        u." + UsersColumns.AVATAR + "," +
                             "        n." + NotificationsColumns.CREATION_DATE + "," +
                             "        n." + NotificationsColumns.USER_ID_PARAM + "," +
                             "        n." + NotificationsColumns.IDEA_ID_PARAM + "," +
                             "        n." + NotificationsColumns.GROUP_ID_PARAM +
                             "  from " + TABLE_NAME + " n " +
                             "  left join " + UsersRepository.TABLE_NAME + " u " +
                             "    on u." + UsersColumns.ID + " = " + NotificationsColumns.OWNER;

        /** The internal query. */
        StringBuilder whereClause = new StringBuilder(" where 1 = 1 ");

        /** Parameters list */
        List<Object> parameters = new ArrayList<>();

        /**
         * Adds an identifier filter.
         *
         * @param id The notification identifier.
         * @return The builder instance.
         */
        public NotificationFetcher whereId(Long id) {
            whereClause.append(" and n.id = ? ");
            parameters.add(id);
            return this;
        }

        /**
         * Adds a filter on the notification's owner.
         *
         * @param owner The notification owner.
         * @return The builder instance.
         */
        public NotificationFetcher whereOwner(User owner) {
            whereClause.append(" and n.owner = ? ");
            parameters.add(owner.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's type.
         *
         * @param type The notification type.
         * @return The builder instance.
         */
        public NotificationFetcher whereType(NType type) {
            whereClause.append(" and n.type = ? ");
            parameters.add(type.name());
            return this;
        }

        /**
         * Adds a filter on the notification's user parameter.
         *
         * @param user The notification's user parameter.
         * @return The builder instance.
         */
        public NotificationFetcher whereUser(User user) {
            whereClause.append(" and n.user_id_param = ? ");
            parameters.add(user.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's idea parameter.
         *
         * @param idea The notification's idea parameter.
         * @return The builder instance.
         */
        public NotificationFetcher whereIdea(Idee idea) {
            whereClause.append(" and n.idea_id_param = ? ");
            parameters.add(idea.getId());
            return this;
        }

        /**
         * Adds a filter on the notification's group idea parameter.
         *
         * @param group The notification's group idea parameter.
         */
        public void whereGroupIdea(IdeaGroup group) {
            whereClause.append(" and n.group_id_param = ? ");
            parameters.add(group.getId());
        }

        /**
         * Adds a filter on whether this notification was read or not.
         *
         * @param isRead True to filter on the ones that were read, false for unread.
         * @return The builder instance.
         */
        public NotificationFetcher whereRead(boolean isRead) {
            whereClause.append(" and n.is_unread = ? ");
            parameters.add(isRead ? "N" : "Y");
            return this;
        }

        /**
         * @return True if the filter selection returns at least one row.
         */
        public boolean hasAny() {
            final String fullQuery = query + whereClause.toString();
            try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), fullQuery)) {
                ps.bindParameters(parameters.toArray());
                if (ps.execute()) {
                    return ps.getResultSet().next();
                }
            } catch (SQLException e) {
                logger.warn("Fail to fetch the notifications...", e);
            }
            return false;
        }

        /**
         * @param resultSet  The notification ResultSet.
         * @param columnName The column name to read.
         * @return The integer value or null if not parsable or if the column's value is null.
         */
        private static Integer getIntegerValueOf(ResultSet resultSet, String columnName) throws SQLException {
            try {
                return Integer.valueOf(resultSet.getString(columnName));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        /**
         * @param resultSet The notification ResultSet.
         * @return The new memory notification from the value read from the database.
         */
        private static Notification buildIt(ResultSet resultSet) throws SQLException {

            // Récupération des paramètres depuis le ResultSet
            String type = resultSet.getString(NotificationsColumns.TYPE.name());
            long id = resultSet.getLong(NotificationsColumns.ID.name());
            User owner = new User(resultSet.getInt("user_id"),
                                  resultSet.getString(UsersColumns.NAME.name()),
                                  resultSet.getString(UsersColumns.EMAIL.name()),
                                  resultSet.getDate(UsersColumns.BIRTHDAY.name()),
                                  resultSet.getString(UsersColumns.AVATAR.name()));
            Instant creation = Optional.ofNullable(resultSet.getTimestamp(NotificationsColumns.CREATION_DATE.name()))
                                       .map(Timestamp::toInstant)
                                       .orElse(null);

            // Reading the parameters
            final Integer userId = getIntegerValueOf(resultSet, USER_ID_PARAM.name());
            User userParameter = userId == null ? null : UsersRepository.getUser(userId).orElse(null);
            final Integer ideaId = getIntegerValueOf(resultSet, IDEA_ID_PARAM.name());
            Idee ideaParameter = IdeesRepository.getIdea(ideaId)
                                                .orElse(IdeesRepository.getDeletedIdea(ideaId).orElse(null));
            final Integer groupId = getIntegerValueOf(resultSet, GROUP_ID_PARAM.name());
            IdeaGroup groupParameter = GroupIdeaRepository.getGroupDetails(groupId).orElse(null);

            return NotificationFactory.builder(NType.valueOf(type))
                                      .withAnID(id)
                                      .belongsTo(owner)
                                      .withUserParameter(userParameter)
                                      .withIdeaParameter(ideaParameter)
                                      .withGroupParameter(groupParameter)
                                      .withCreationTime(creation)
                                      .build();
        }

        /**
         * @return The notification list based on previous criteria.
         */
        public List<Notification> fetch() {
            List<Notification> notifications = new ArrayList<>();
            final String fullQuery = query + whereClause.toString();
            logger.debug("[Perf] fetch. Query: {}. Parameters: {}", fullQuery, parameters);
            try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), fullQuery)) {
                ps.bindParameters(parameters.toArray());
                if (ps.execute()) {
                    logger.trace("[Perf] Execution completed! Building the result...");
                    ResultSet res = ps.getResultSet();
                    while (res.next()) {
                        try {
                            notifications.add(buildIt(res));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Unknown type...", e);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.warn("Fail to fetch the notifications...", e);
            }
            logger.trace("[Perf] Completed! Created {} objects.", notifications.size());
            return notifications;
        }
    }

    /**
     * @param notification The notification.
     * @return The list of corresponding notifications, if any.
     */
    public static List<Notification> findNotificationsMatching(Notification notification) {
        NotificationFetcher fetcher = fetcher().whereOwner(notification.getOwner())
                                               .whereType(notification.getType());
        notification.getUserParameter().ifPresent(fetcher::whereUser);
        notification.getIdeaParameter().ifPresent(fetcher::whereIdea);
        notification.getGroupParameter().ifPresent(fetcher::whereGroupIdea);
        return fetcher.fetch();
    }

    /**
     * @param owner The notification owner.
     * @return All notifications for this user.
     */
    public static List<Notification> getUserNotifications(User owner, NType type) {
        return fetcher().whereOwner(owner).whereType(type).fetch();
    }

    /**
     * @param user The user.
     * @return All read notifications of this user.
     */
    public static List<Notification> getUserReadNotifications(User user) {
        return fetcher().whereOwner(user).whereRead(true).fetch();
    }

    /**
     * @param user The user.
     * @return All unread notifications of this user.
     */
    public static List<Notification> getUserUnReadNotifications(User user) {
        return fetcher().whereOwner(user).whereRead(false).fetch();
    }

    /**
     * @param notificationId The notification id.
     * @return The notification corresponding to this id.
     */
    public static Optional<Notification> getNotification(long notificationId) {
        return fetcher().whereId(notificationId).fetch().stream().findFirst();
    }

    // -------------------------------------------------------------------------------
    // --------------------          ADMIN Notifications          --------------------
    // -------------------------------------------------------------------------------

    /**
     * Manually sends a new notification to the ADMIN.
     *
     * @param type    The notification type.
     * @param message The message.
     */
    private static void sendAdminNotification(String type, String message) {

        // Insert a DB notification
        logger.info("Envoie d'une notification admin de type {}...", type);

        // Send emails to the ADMIN
        String query = "select u." + UsersColumns.EMAIL +
                       "  from " + UsersRepository.TABLE_NAME + " u " +
                       "  join USER_ROLES ur on ur." + UserRolesColumns.EMAIL + " = u." + UsersColumns.EMAIL + " " +
                       "  where ur." + UserRolesColumns.ROLE + " = ?";

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters("ROLE_ADMIN");
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    String emailAdress = res.getString(UsersColumns.EMAIL.name());
                    String messageTemplate = notificationProperties.get("mail_template").toString();
                    String body = messageTemplate.replaceAll("\\$\\$text\\$\\$", Matcher.quoteReplacement(message));
                    EmailSender.sendEmail(emailAdress, "Nos idées de cadeaux - Admin notification...", body);
                }
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
        logger.info(MessageFormat.format("New person notification ! Message: {0}", message));
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

    // -------------------------------------------------------------------------
    // --------------------          Read / Unread          --------------------
    // -------------------------------------------------------------------------

    /**
     * Set the notification as read.
     *
     * @param notif The notification.
     */
    public static void setRead(Notification notif) throws SQLException {
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
    public static void setUnread(Notification notif) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ? ",
                                                   TABLE_NAME,
                                                   NotificationsColumns.IS_UNREAD,
                                                   NotificationsColumns.ID),
                              "Y",
                              notif.id);
    }

    static {
        InputStream input = NotificationsRepository.class.getResourceAsStream("/notif.properties");
        try {
            notificationProperties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.warn(e);
        }
    }
}
