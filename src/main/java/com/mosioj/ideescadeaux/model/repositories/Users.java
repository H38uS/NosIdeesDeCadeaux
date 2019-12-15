package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.AVATAR;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.BIRTHDAY;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.CREATION_DATE;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.EMAIL;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.ID;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.LAST_LOGIN;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.NAME;
import static com.mosioj.ideescadeaux.model.repositories.columns.UsersColumns.PASSWORD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UserRelationsColumns;
import com.mosioj.ideescadeaux.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.utils.database.NoRowsException;
import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

/**
 * Représente la table de personnes.
 *
 * @author Jordan Mosio
 */
public class Users extends Table {

    public static final String TABLE_NAME = "USERS";
    private static final Logger LOGGER = LogManager.getLogger(Users.class);

    /**
     * Inserts a new person into the database !
     *
     * @param email       The user's email.
     * @param digestedPwd The obfuscated password.
     * @param name        The user name.
     * @return the created user id.
     */
    public int addNewPersonne(String email, String digestedPwd, String name) throws SQLException {
        int userId = getDb().executeUpdateGeneratedKey(MessageFormat.format(
                "insert into {0} ({1},{2},{3},{4},{5}) values (?, ?, now(), now(), ?)",
                TABLE_NAME,
                EMAIL,
                PASSWORD,
                LAST_LOGIN,
                CREATION_DATE,
                NAME),
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
    public User getUser(int id) throws SQLException {

        User user = null;
        String query = MessageFormat.format("select {0}, {1}, {2}, {3}, {5} from {4} where {0} = ?",
                                            ID,
                                            NAME,
                                            EMAIL,
                                            BIRTHDAY,
                                            TABLE_NAME,
                                            AVATAR);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {
            ps.bindParameters(id);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    user = new User(res.getInt(ID.name()),
                                    res.getString(NAME.name()),
                                    res.getString(EMAIL.name()),
                                    res.getDate(BIRTHDAY.name()),
                                    res.getString(AVATAR.name()));
                }
            }
        }

        return user;
    }

    /**
     * @param email The identifier of the person (currently the email).
     * @return This person's id.
     */
    public int getId(String email) throws SQLException, NoRowsException {
        return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, EMAIL),
                                 email);
    }

    /**
     * @param nameOrEmail The name or email of the person..
     * @return This person's id.
     */
    public int getIdFromNameOrEmail(String nameOrEmail) throws SQLException, NoRowsException {
        return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ? or {3} = ? limit 1",
                                                      ID,
                                                      TABLE_NAME,
                                                      EMAIL,
                                                      NAME),
                                 nameOrEmail,
                                 nameOrEmail);
    }

    /**
     * Persists the user configuration in DB.
     *
     * @param user The user to update.
     */
    public void update(User user) throws SQLException {
        LOGGER.trace(MessageFormat.format("Updating user {0}. Avatar: {1}", user.id, user.avatar));
        String previousEmail = getDb().selectString(MessageFormat.format("select {0} from {1} where {2} = ?",
                                                                         EMAIL,
                                                                         TABLE_NAME,
                                                                         ID),
                                                    user.id);
        String query = MessageFormat.format("update {0} set {1} = ?, {2} = ?, {3} = ?, {5} = ? where {4} = ?",
                                            TABLE_NAME,
                                            EMAIL,
                                            NAME,
                                            BIRTHDAY,
                                            ID,
                                            AVATAR);
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
    public List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String query = MessageFormat.format("select {0},{1},{2},{3},{4},{5} ",
                                            ID,
                                            NAME,
                                            EMAIL,
                                            AVATAR,
                                            CREATION_DATE,
                                            LAST_LOGIN) +
                       MessageFormat.format("  from {0} u ", TABLE_NAME) +
                       MessageFormat.format(" order by {0}, {1}, {2} ", NAME, EMAIL, ID);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(),
                                                                    query)) {

            if (!ps.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = ps.getResultSet();
            while (res.next()) {

                Timestamp creation = null;
                try {
                    creation = res.getTimestamp(CREATION_DATE.name());
                } catch (SQLException ignored) {
                }

                Timestamp lastLogin = null;
                try {
                    lastLogin = res.getTimestamp(LAST_LOGIN.name());
                } catch (SQLException ignored) {
                }

                users.add(new User(res.getInt(ID.name()),
                                   res.getString(NAME.name()),
                                   res.getString(EMAIL.name()),
                                   res.getString(AVATAR.name()),
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
    public List<User> getUsers(String nameToMatch,
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
        query.append(MessageFormat.format("select {0},{1},{2},{3} ", ID, NAME, EMAIL, AVATAR));
        query.append(MessageFormat.format("  from {0} u ", TABLE_NAME));
        query.append(MessageFormat.format(" where (lower({0}) like ? ESCAPE ''!''   ", NAME));
        query.append(MessageFormat.format("    or  lower({0}) like ? ESCAPE ''!'' ) ", EMAIL));
        query.append(MessageFormat.format("   and {0} <> ? ", ID));
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(MessageFormat.format(" select 1 from {0}  ", UserRelations.TABLE_NAME));
            query.append(MessageFormat.format("  where {0} = ?     ", UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("    and {0} = u.{1} ", UserRelationsColumns.SECOND_USER, ID));
            query.append("   ) ");
        }
        query.append(MessageFormat.format(" order by {0}, {1}, {2} ", NAME, EMAIL, ID));
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
                users.add(new User(res.getInt(ID.name()),
                                   res.getString(NAME.name()),
                                   res.getString(EMAIL.name()),
                                   res.getString(AVATAR.name())));
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
    public int getTotalUsers(String nameToMatch, int userIdToSkip, boolean selectOnlyNonFriends) throws SQLException {

        nameToMatch = sanitizeSQLLike(nameToMatch);

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("select {0} ", "count(*)"));
        query.append(MessageFormat.format("  from {0} u ", TABLE_NAME));
        query.append(MessageFormat.format(" where ({0} like ? ESCAPE ''!''   ", NAME));
        query.append(MessageFormat.format("    or  {0} like ? ESCAPE ''!'' ) ", EMAIL));
        query.append(MessageFormat.format("   and {0} <> ? ", ID));
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(MessageFormat.format(" select 1 from {0}  ", UserRelations.TABLE_NAME));
            query.append(MessageFormat.format("  where {0} = ?     ", UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("    and {0} = u.{1} ", UserRelationsColumns.SECOND_USER, ID));
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
    public void updatePassword(int userId, String digestedPwd) {
        getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ?", TABLE_NAME, PASSWORD, ID),
                              digestedPwd,
                              userId);
    }

    public void deleteUser(User user) throws SQLException {

        LOGGER.info(MessageFormat.format("Suppression de {0}...", user));
        int userId = user.id;

        // Suppression des commentaires
        new Comments().deleteAll(userId);

        // Suppression des participations aux groupes
        new GroupIdea().removeUserFromAllGroups(user);

        // Suppression des idées
        // Suppression de l'historique des idées
        new Idees().removeAll(userId);

        // Suppression des notifications
        new Notifications().removeAll(userId);

        // Suppression des relations parents - enfants
        new ParentRelationship().deleteAllRelationForUser(userId);

        // Suppresion des questions
        new Questions().deleteAll(userId);

        // Suppression des demandes de modifications de mdp
        new UserChangePwdRequest().deleteAssociation(userId);

        // Suppression des paramètres utilisateurs
        new UserParameters().deleteAllUserParameters(userId);

        // Suppression des relations, des suggestions et des demandes
        new UserRelations().removeAllAssociationsTo(userId);
        new UserRelationsSuggestion().removeAllFromAndTo(userId);
        new UserRelationRequests().removeAllFromAndTo(userId);

        // Suppression des roles
        getDb().executeUpdate(MessageFormat.format("delete from USER_ROLES where {0} = ?", UserRolesColumns.EMAIL),
                              user.email);

        // Et !! Suppression du user
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, ID), userId);
    }

    /**
     * Lorsque l'on vient de se connecter.
     *
     * @param email The user's email.
     */
    public void touch(String email) {
        getDb().executeUpdate("update " + TABLE_NAME + " set " + LAST_LOGIN + " = now() where " + EMAIL + " = ?",
                              email);
    }

}
