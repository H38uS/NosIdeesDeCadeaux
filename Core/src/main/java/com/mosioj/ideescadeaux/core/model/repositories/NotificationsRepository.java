package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserParameter;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.utils.EmailSender;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;

public class NotificationsRepository {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(NotificationsRepository.class);

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
     */
    public static Notification add(Notification notification) {

        logger.info("Creating notification {} for user {}. Parameters: user={}, idea={}, group={}.",
                    notification.getType(),
                    notification.getOwner(),
                    notification.getUserParameter(),
                    notification.getIdeaParameter(),
                    notification.getGroupParameter());
        try {
            NotificationActivation activation = getActivationType(notification.getOwner().getId(), notification);

            // Insertion en base
            if (activation == NotificationActivation.SITE || activation == NotificationActivation.EMAIL_SITE) {
                HibernateUtil.saveit(notification);
            }

            // Envoie de la notification par email si besoin
            if (activation == NotificationActivation.EMAIL || activation == NotificationActivation.EMAIL_SITE) {
                sendEmail(notification);
            }
        } catch (SQLException e) {
            logger.warn("Exception while trying to add a notification.", e);
        }

        return notification;
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
                                                         .map(UserParameter::getParameterName)
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
        StringBuilder query = new StringBuilder("delete from NOTIFICATIONS where 1 = 1 ");

        /** Parameters list */
        List<Object> parameters = new ArrayList<>();

        /**
         * Adds an identifier filter.
         *
         * @param id The notification identifier.
         * @return The builder instance.
         */
        public NotificationTerminator whereId(int id) {
            parameters.add(id);
            query.append(" and id = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's owner.
         *
         * @param owner The notification owner.
         * @return The builder instance.
         */
        public NotificationTerminator whereOwner(User owner) {
            parameters.add(owner);
            query.append(" and owner = ?").append(parameters.size()).append(" ");
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
            query.append(" and type in (");
            for (int i = 0; i < typesList.size(); i++) {
                query.append("?").append(parameters.size() + i + 1).append(",");
            }
            query.deleteCharAt(query.length() - 1);
            query.append(")");
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
            parameters.add(user.getId());
            query.append(" and user_id_param = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's idea parameter.
         *
         * @param idea The notification's idea parameter.
         * @return The builder instance.
         */
        public NotificationTerminator whereIdea(Idee idea) {
            parameters.add(idea.getId());
            query.append(" and idea_id_param = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's group idea parameter.
         *
         * @param group The notification's group idea parameter.
         * @return The builder instance.
         */
        public NotificationTerminator whereGroupIdea(IdeaGroup group) {
            parameters.add(group.getId());
            query.append(" and group_id_param = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Runs the query built so far.
         */
        public void terminates() {
            int nb = HibernateUtil.doSomeExecutionWork(s -> {
                Query<?> sqlQuery = s.createQuery(query.toString());
                HibernateUtil.bindParameters(sqlQuery, parameters.toArray());
                return sqlQuery.executeUpdate();
            });
            logger.debug("{} notifications deleted using {}, and {}.", nb, parameters, query);
        }
    }

    // -------------------------------------------------------------------
    // --------------------          FETCHER          --------------------
    // -------------------------------------------------------------------

    /**
     * @param user The user.
     * @return The number of notification this user has.
     */
    public static int getUserNotificationCountWithChildren(User user) {
        final String queryText = """
                select count(*)
                  from (
                        select 1
                          from NOTIFICATIONS
                         where owner = :owner
                         union all
                        select 1
                          from NOTIFICATIONS n
                          left join PARENT_RELATIONSHIP p on n.owner = child_id
                         where p.parent_id = :owner
                        ) c
                """;
        // FIXME revenir avec un Long.class quand PARENT_RELATIONSHIP est une entité hibernate
        final Optional<Integer> count = HibernateUtil.<Object>doQueryOptional(s -> s.createNativeQuery(queryText)
                                                                                    .setParameter("owner", user.getId())
                                                                                    .uniqueResultOptional())
                                                     .map(res -> {
                                                         if (res instanceof BigInteger)
                                                             return ((BigInteger) res).intValue();
                                                         return (Integer) res;
                                                     });
        return count.orElse(0);
    }

    /**
     * @return A new fetcher builder.
     */
    public static NotificationFetcher fetcher() {
        return new NotificationFetcher();
    }

    public static class NotificationFetcher {

        final String query = """
                select  n
                  from NOTIFICATIONS n
                  left join fetch n.owner
                  left join fetch n.userParameter
                  left join fetch n.ideaParameter
                  left join fetch n.groupParameter
                """;

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
        public NotificationFetcher whereId(int id) {
            parameters.add(id);
            whereClause.append(" and n.id = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's owner.
         *
         * @param owner The notification owner.
         * @return The builder instance.
         */
        public NotificationFetcher whereOwner(User owner) {
            parameters.add(owner);
            whereClause.append(" and n.owner = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's type.
         *
         * @param type The notification type.
         * @return The builder instance.
         */
        public NotificationFetcher whereType(NType type) {
            parameters.add(type);
            whereClause.append(" and n.type = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's user parameter.
         *
         * @param user The notification's user parameter.
         * @return The builder instance.
         */
        public NotificationFetcher whereUser(User user) {
            parameters.add(user);
            whereClause.append(" and n.userParameter = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's idea parameter.
         *
         * @param idea The notification's idea parameter.
         * @return The builder instance.
         */
        public NotificationFetcher whereIdea(Idee idea) {
            parameters.add(idea);
            whereClause.append(" and n.ideaParameter = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * Adds a filter on the notification's group idea parameter.
         *
         * @param group The notification's group idea parameter.
         */
        public void whereGroupIdea(IdeaGroup group) {
            parameters.add(group);
            whereClause.append(" and n.groupParameter = ?").append(parameters.size()).append(" ");
        }

        /**
         * Adds a filter on whether this notification was read or not.
         *
         * @param isRead True to filter on the ones that were read, false for unread.
         * @return The builder instance.
         */
        public NotificationFetcher whereRead(boolean isRead) {
            parameters.add(isRead ? "N" : "Y");
            whereClause.append(" and n.isUnread = ?").append(parameters.size()).append(" ");
            return this;
        }

        /**
         * @return True if the filter selection returns at least one row.
         */
        public boolean hasAny() {
            final String fullQuery = "select n from NOTIFICATIONS n " + whereClause.toString();
            return HibernateUtil.doesReturnRows(fullQuery, parameters.toArray());
        }

        /**
         * @return The notification list based on previous criteria.
         */
        public List<Notification> fetch() {
            final String fullQuery = query + whereClause.toString();
            logger.trace("[Perf] fetch. Query: {}. Parameters: {}", fullQuery, parameters);
            List<Notification> notifications = HibernateUtil.doQueryFetch(s -> {
                Query<Notification> sqlQuery = s.createQuery(fullQuery, Notification.class);
                HibernateUtil.bindParameters(sqlQuery, parameters.toArray());
                return sqlQuery.list();
            });
            logger.trace("[Perf] Execution completed! Building the result...");
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
    public static Optional<Notification> getNotification(int notificationId) {
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
        String messageTemplate = notificationProperties.get("mail_template").toString();
        String body = messageTemplate.replaceAll("\\$\\$text\\$\\$", Matcher.quoteReplacement(message));
        UsersRepository.getAllAdmins()
                       .stream()
                       .map(User::getEmail)
                       .forEach(email -> EmailSender.sendEmail(email,
                                                               "Nos idées de cadeaux - Admin notification...",
                                                               body));
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
    public static void setRead(Notification notif) {
        notif.isUnread = "N";
        notif.readOn = LocalDateTime.now();
        HibernateUtil.update(notif);
    }

    /**
     * Set the notification as unread.
     *
     * @param notif The notification.
     */
    public static void setUnread(Notification notif) {
        notif.isUnread = "Y";
        HibernateUtil.update(notif);
    }

    static {
        InputStream input = NotificationsRepository.class.getResourceAsStream("/notif.properties");
        try {
            if (input == null) {
                logger.error("Cannot find the notif.properties resources...");
            } else {
                notificationProperties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            logger.warn(e);
        }
    }
}
