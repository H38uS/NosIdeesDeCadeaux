package com.mosioj.ideescadeaux.model.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.ideescadeaux.model.repositories.columns.UserRelationsColumns;
import com.mosioj.ideescadeaux.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.model.database.NoRowsException;
import com.mosioj.ideescadeaux.model.database.PreparedStatementIdKdo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;

/**
 * Représente la table de personnes.
 *
 * @author Jordan Mosio
 */
public class UsersRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USERS";
    private static final Logger LOGGER = LogManager.getLogger(UsersRepository.class);

    private UsersRepository() {
        // Forbidden
    }

    /**
     * Inserts a new person into the database !
     *
     * @param email       The user's email.
     * @param digestedPwd The obfuscated password.
     * @param name        The user name.
     * @return the created user id.
     */
    public static int addNewPersonne(String email, String digestedPwd, String name) {
        int userId = getDb().executeUpdateGeneratedKey(MessageFormat.format(
                "insert into {0} ({1},{2},{3},{4},{5}) values (?, ?, now(), now(), ?)",
                TABLE_NAME,
                UsersColumns.EMAIL,
                UsersColumns.PASSWORD,
                UsersColumns.LAST_LOGIN,
                UsersColumns.CREATION_DATE,
                UsersColumns.NAME),
                                                       email,
                                                       digestedPwd,
                                                       name);
        getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into USER_ROLES ({0},{1}) values (?, ?)",
                                                               UserRolesColumns.EMAIL,
                                                               UserRolesColumns.ROLE),
                                          email,
                                          "ROLE_USER");
        return userId;
    }

    /**
     * @param id The user's id.
     * @return The user corresponding to this ID or null if not found.
     */
    public static User getUser(int id) throws SQLException {

        User user = null;
        String query = MessageFormat.format("select {0}, {1}, {2}, {3}, {5} from {4} where {0} = ?",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.BIRTHDAY,
                                            TABLE_NAME,
                                            UsersColumns.AVATAR);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(id);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    user = new User(res.getInt(UsersColumns.ID.name()),
                                    res.getString(UsersColumns.NAME.name()),
                                    res.getString(UsersColumns.EMAIL.name()),
                                    res.getDate(UsersColumns.BIRTHDAY.name()),
                                    res.getString(UsersColumns.AVATAR.name()));
                }
            }
        }

        return user;
    }

    /**
     * @param email The identifier of the person (currently the email).
     * @return This person's id.
     */
    public static int getId(String email) throws NoRowsException {
        return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ?", UsersColumns.ID, TABLE_NAME, UsersColumns.EMAIL),
                                 email);
    }

    /**
     * @param nameOrEmail The name or email of the person..
     * @return This person's id.
     */
    public static int getIdFromNameOrEmail(String nameOrEmail) throws NoRowsException {
        return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ? or {3} = ? limit 1",
                                                      UsersColumns.ID,
                                                      TABLE_NAME,
                                                      UsersColumns.EMAIL,
                                                      UsersColumns.NAME),
                                 nameOrEmail,
                                 nameOrEmail);
    }

    /**
     * Persists the user configuration in DB.
     *
     * @param user The user to update.
     */
    public static void update(User user) {
        LOGGER.trace(MessageFormat.format("Updating user {0}. Avatar: {1}", user.id, user.avatar));
        String previousEmail = getDb().selectString(MessageFormat.format("select {0} from {1} where {2} = ?",
                                                                         UsersColumns.EMAIL,
                                                                         TABLE_NAME,
                                                                         UsersColumns.ID),
                                                    user.id);
        String query = MessageFormat.format("update {0} set {1} = ?, {2} = ?, {3} = ?, {5} = ? where {4} = ?",
                                            TABLE_NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.NAME,
                                            UsersColumns.BIRTHDAY,
                                            UsersColumns.ID,
                                            UsersColumns.AVATAR);
        getDb().executeUpdate(query, user.email, user.name, user.birthday, user.avatar, user.id);
        if (!previousEmail.equals(user.email)) {
            getDb().executeUpdate(MessageFormat.format("update USER_ROLES set {0} = ? where {1} = ? ",
                                                       UserRolesColumns.EMAIL,
                                                       UserRolesColumns.EMAIL),
                                  user.email,
                                  previousEmail);
        }
    }

    /**
     * @return All the users in DB. Only used for administration
     */
    public static List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String query = MessageFormat.format("select {0},{1},{2},{3},{4},{5} ",
                                            UsersColumns.ID,
                                            UsersColumns.NAME,
                                            UsersColumns.EMAIL,
                                            UsersColumns.AVATAR,
                                            UsersColumns.CREATION_DATE,
                                            UsersColumns.LAST_LOGIN) +
                       MessageFormat.format("  from {0} u ", TABLE_NAME) +
                       MessageFormat.format(" order by {0}, {1}, {2} ", UsersColumns.NAME, UsersColumns.EMAIL, UsersColumns.ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {

            if (!ps.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = ps.getResultSet();
            while (res.next()) {

                Timestamp creation = null;
                try {
                    creation = res.getTimestamp(UsersColumns.CREATION_DATE.name());
                } catch (SQLException ignored) {
                }

                Timestamp lastLogin = null;
                try {
                    lastLogin = res.getTimestamp(UsersColumns.LAST_LOGIN.name());
                } catch (SQLException ignored) {
                }

                users.add(new User(res.getInt(UsersColumns.ID.name()),
                                   res.getString(UsersColumns.NAME.name()),
                                   res.getString(UsersColumns.EMAIL.name()),
                                   res.getString(UsersColumns.AVATAR.name()),
                                   creation,
                                   lastLogin));
            }

        }

        return users;
    }

    /**
     * Excludes the current user.
     *
     * @param nameToMatch          The name or email.
     * @param userIdToSkip         The id not to select.
     * @param selectOnlyNonFriends True to select only non friends.
     * @param firstRow             The first index.
     * @param limit                Maximum result size.
     * @return The list of users.
     */
    public static List<User> getUsers(String nameToMatch,
                                      int userIdToSkip,
                                      boolean selectOnlyNonFriends,
                                      int firstRow,
                                      int limit) throws SQLException {

        List<User> users = new ArrayList<>();
        LOGGER.debug(MessageFormat.format("Getting users from search token: ''{0}'' for user {1}.",
                                          nameToMatch,
                                          userIdToSkip));

        nameToMatch = sanitizeSQLLike(nameToMatch);

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("select {0},{1},{2},{3} ", UsersColumns.ID, UsersColumns.NAME, UsersColumns.EMAIL, UsersColumns.AVATAR));
        query.append(MessageFormat.format("  from {0} u ", TABLE_NAME));
        query.append(MessageFormat.format(" where (lower({0}) like ? ESCAPE ''!''   ", UsersColumns.NAME));
        query.append(MessageFormat.format("    or  lower({0}) like ? ESCAPE ''!'' ) ", UsersColumns.EMAIL));
        query.append(MessageFormat.format("   and {0} <> ? ", UsersColumns.ID));
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(MessageFormat.format(" select 1 from {0}  ", UserRelationsRepository.TABLE_NAME));
            query.append(MessageFormat.format("  where {0} = ?     ", UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("    and {0} = u.{1} ", UserRelationsColumns.SECOND_USER, UsersColumns.ID));
            query.append("   ) ");
        }
        query.append(MessageFormat.format(" order by {0}, {1}, {2} ", UsersColumns.NAME, UsersColumns.EMAIL, UsersColumns.ID));
        query.append(" 						LIMIT ?, ? ");

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            if (selectOnlyNonFriends) {
                ps.bindParameters(nameToMatch, nameToMatch, userIdToSkip, userIdToSkip, firstRow, limit);
            } else {
                ps.bindParameters(nameToMatch, nameToMatch, userIdToSkip, firstRow, limit);
            }

            if (!ps.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = ps.getResultSet();
            while (res.next()) {
                users.add(new User(res.getInt(UsersColumns.ID.name()),
                                   res.getString(UsersColumns.NAME.name()),
                                   res.getString(UsersColumns.EMAIL.name()),
                                   res.getString(UsersColumns.AVATAR.name())));
            }
        }

        return users;
    }

    /**
     * Excludes the current user.
     *
     * @param nameToMatch          Name or email.
     * @param userIdToSkip         The id not to select.
     * @param selectOnlyNonFriends True to select only non friends.
     * @return The number of users matching this name/email.
     */
    public static int getTotalUsers(String nameToMatch, int userIdToSkip, boolean selectOnlyNonFriends) throws SQLException {

        nameToMatch = sanitizeSQLLike(nameToMatch);

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("select {0} ", "count(*)"));
        query.append(MessageFormat.format("  from {0} u ", TABLE_NAME));
        query.append(MessageFormat.format(" where ({0} like ? ESCAPE ''!''   ", UsersColumns.NAME));
        query.append(MessageFormat.format("    or  {0} like ? ESCAPE ''!'' ) ", UsersColumns.EMAIL));
        query.append(MessageFormat.format("   and {0} <> ? ", UsersColumns.ID));
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(MessageFormat.format(" select 1 from {0}  ", UserRelationsRepository.TABLE_NAME));
            query.append(MessageFormat.format("  where {0} = ?     ", UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("    and {0} = u.{1} ", UserRelationsColumns.SECOND_USER, UsersColumns.ID));
            query.append("   ) ");
        }

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            if (selectOnlyNonFriends) {
                ps.bindParameters(nameToMatch, nameToMatch, userIdToSkip, userIdToSkip);
            } else {
                ps.bindParameters(nameToMatch, nameToMatch, userIdToSkip);
            }

            if (!ps.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = ps.getResultSet();
            if (res.next()) {
                return res.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Update the user password.
     *
     * @param userId      The user id.
     * @param digestedPwd The new obfuscated password.
     */
    public static void updatePassword(int userId, String digestedPwd) {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ?", TABLE_NAME, UsersColumns.PASSWORD, UsersColumns.ID),
                              digestedPwd,
                              userId);
    }

    public static void deleteUser(User user) {

        LOGGER.info(MessageFormat.format("Suppression de {0}...", user));
        int userId = user.id;

        // Suppression des commentaires
        CommentsRepository.deleteAll(userId);

        // Suppression des participations aux groupes
        GroupIdeaRepository.removeUserFromAllGroups(user);

        // Suppression des idées
        // Suppression de l'historique des idées
        IdeesRepository.removeAll(userId);

        // Suppression des notifications
        NotificationsRepository.removeAll(userId);

        // Suppression des relations parents - enfants
        ParentRelationshipRepository.deleteAllRelationForUser(userId);

        // Suppresion des questions
        QuestionsRepository.deleteAll(userId);

        // Suppression des demandes de modifications de mdp
        UserChangePwdRequestRepository.deleteAssociation(userId);

        // Suppression des paramètres utilisateurs
        UserParametersRepository.deleteAllUserParameters(userId);

        // Suppression des relations, des suggestions et des demandes
        UserRelationsRepository.removeAllAssociationsTo(userId);
        UserRelationsSuggestionRepository.removeAllFromAndTo(userId);
        UserRelationRequestsRepository.removeAllFromAndTo(userId);

        // Suppression des roles
        getDb().executeUpdate(MessageFormat.format("delete from USER_ROLES where {0} = ?", UserRolesColumns.EMAIL),
                              user.email);

        // Et !! Suppression du user
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, UsersColumns.ID), userId);
    }

    /**
     * Lorsque l'on vient de se connecter.
     *
     * @param email The user's email.
     */
    public static void touch(String email) {
        getDb().executeUpdate("update " + TABLE_NAME + " set " + UsersColumns.LAST_LOGIN + " = now() where " + UsersColumns.EMAIL + " = ?",
                              email);
    }

}
