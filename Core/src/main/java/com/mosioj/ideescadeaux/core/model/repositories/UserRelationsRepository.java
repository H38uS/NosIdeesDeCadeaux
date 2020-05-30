package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.Relation;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRelationsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USER_RELATIONS";
    private static final Logger logger = LogManager.getLogger(UserRelationsRepository.class);

    private UserRelationsRepository() {
        // Forbidden
    }

    /**
     * @param userId      The user id.
     * @param nameOrEmail The token to search for.
     * @return The number of user in this user network.
     */
    public static int getRelationsCount(int userId, String nameOrEmail) {

        String query = "select count(*) " +
                       "from {0} urr " +
                       "left join {1} u1 on u1.id = urr.{2} " +
                       "where {3} = ? " +
                       "  and (lower(u1.{4}) like ? ESCAPE ''!'' or lower(u1.{5}) like ? ESCAPE ''!'') ";
        String formatedQuery = MessageFormat.format(
                query,
                TABLE_NAME,
                UsersRepository.TABLE_NAME,
                UserRelationsColumns.SECOND_USER,
                UserRelationsColumns.FIRST_USER,
                UsersColumns.NAME,
                UsersColumns.EMAIL);

        return getDb().selectCountStar(formatedQuery,
                                       userId,
                                       sanitizeSQLLike(nameOrEmail),
                                       sanitizeSQLLike(nameOrEmail));
    }

    /**
     * @param user The user.
     * @return The number of user in this user network.
     */
    public static int getRelationsCount(User user) {
        return getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ?",
                                                            TABLE_NAME,
                                                            UserRelationsColumns.FIRST_USER), user.id);
    }

    /**
     * @param user The user.
     * @return All relations of the given user.
     */
    public static List<Relation> getRelations(int user) throws SQLException {
        return getRelations(user, -1, -1);
    }

    /**
     * @param user            The user.
     * @param firstRow        The first row to select.
     * @param maxNumberOfRows Number of results to retrieve.
     * @return The list of relations this use has.
     */
    public static List<Relation> getRelations(int user, int firstRow, int maxNumberOfRows) throws SQLException {

        List<Relation> relations = new ArrayList<>();

        String query =
                "select {1}, {2}," +
                "       u1.{4} as first_name,  u1.{5} as first_email,  u1.{6} as first_avatar,  u1.{7} as first_birthday, " +
                "       u2.{4} as second_name, u2.{5} as second_email, u2.{6} as second_avatar, u2.{7} as second_birthday " +
                "  from {0} urr " +
                "  left join {3} u1 on u1.id = urr.{1} " +
                "  left join {3} u2 on u2.id = urr.{2} " +
                " where {1} = ? " +
                " order by coalesce(u2.{4}, u2.{5}) ";

        if (firstRow > -1 && maxNumberOfRows > 0) {
            query += " LIMIT ?, ? ";
        }

        String formatedQuery = MessageFormat.format(query,
                                                    TABLE_NAME,
                                                    UserRelationsColumns.FIRST_USER,
                                                    UserRelationsColumns.SECOND_USER,
                                                    UsersRepository.TABLE_NAME,
                                                    UsersColumns.NAME,
                                                    UsersColumns.EMAIL,
                                                    UsersColumns.AVATAR,
                                                    UsersColumns.BIRTHDAY);
        logger.trace(formatedQuery);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), formatedQuery)) {

            if (firstRow > -1 && maxNumberOfRows > 0) {
                ps.bindParameters(user, firstRow, maxNumberOfRows);
            } else {
                ps.bindParameters(user);
            }

            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {

                    Date firstBirthday = null;
                    try {
                        firstBirthday = res.getDate("first_birthday");
                    } catch (SQLException ignored) {
                        // Plante lorsque vide - nécessaire de catcher
                    }
                    Date secondBirthday = null;
                    try {
                        secondBirthday = res.getDate("second_birthday");
                    } catch (SQLException ignored) {
                        // Plante lorsque vide - nécessaire de catcher
                    }

                    relations.add(new Relation(new User(res.getInt(UserRelationsColumns.FIRST_USER.name()),
                                                        res.getString("first_name"),
                                                        res.getString("first_email"),
                                                        firstBirthday,
                                                        res.getString("first_avatar")),
                                               new User(res.getInt(UserRelationsColumns.SECOND_USER.name()),
                                                        res.getString("second_name"),
                                                        res.getString("second_email"),
                                                        secondBirthday,
                                                        res.getString("second_avatar"))));
                }
            }
        }

        return relations;
    }

    /**
     * @param user        The user.
     * @param inNbDaysMax The number of days to select the birthday.
     * @return The list of users with birthday coming (less than 30 days).
     */
    public static List<User> getCloseBirthday(User user, int inNbDaysMax) throws SQLException {

        List<User> users = new ArrayList<>();
        PreparedStatementIdKdo ps = null;

        StringBuilder query = new StringBuilder();
        query.append(
                "select b.{0}, b.{1}, b.{2}, b.{3}, b.{8}, b.days_before_next_year_birthday, b.days_before_birthday ");
        query.append("from ( ");

        query.append(
                "select a.{0}, a.{1}, a.{2}, a.{3}, a.{8}, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()) +1, ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_next_year_birthday, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()), ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_birthday ");
        query.append("from ( ");
        query.append("select u.{0}, u.{1}, u.{2}, u.{3}, u.{8} ");
        query.append("from {4} urr ");
        query.append("left join {5} u on u.{0} = urr.{7} ");
        query.append("where {6} = ? ");
        query.append(") a ");

        query.append(") b ");
        query.append(
                "where (b.days_before_birthday >= 0 and b.days_before_birthday < ?) or b.days_before_next_year_birthday < ? ");
        query.append("order by b.days_before_next_year_birthday ");

        try {
            String realQuery = MessageFormat.format(query.toString(),
                                                    UsersColumns.ID,
                                                    UsersColumns.NAME,
                                                    UsersColumns.EMAIL,
                                                    UsersColumns.BIRTHDAY,
                                                    TABLE_NAME,
                                                    UsersRepository.TABLE_NAME,
                                                    UserRelationsColumns.FIRST_USER,
                                                    UserRelationsColumns.SECOND_USER,
                                                    UsersColumns.AVATAR);
            logger.trace(realQuery);
            ps = new PreparedStatementIdKdo(getDb(), realQuery);
            ps.bindParameters(user.id, inNbDaysMax, inNbDaysMax);

            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name()),
                                       res.getInt("days_before_birthday") < 0 ? res.getInt(
                                               "days_before_next_year_birthday")
                                               : res.getInt("days_before_birthday")));
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return users;
    }


    /**
     * @param inNbDays Number of days before the birthday.
     * @return The list of users with birthday coming (less than 30 days).
     */
    public static List<User> getBirthday(int inNbDays) throws SQLException {

        List<User> users = new ArrayList<>();

        String query =
                "select b.{0}, b.{1}, b.{2}, b.{3}, b.{5}, b.days_before_next_year_birthday, b.days_before_birthday " +
                "  from ( " +
                "          select a.{0}," +
                "                 a.{1}, " +
                "                 a.{2}," +
                "                 a.{3}," +
                "                 a.{5}," +
                "                 TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()) +1, ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_next_year_birthday," +
                "                 TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()), ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_birthday " +
                "            from (select u.{0}, u.{1}, u.{2}, u.{3}, u.{5} " +
                "                   from {4} u) a " +
                "       ) b " +
                " where b.days_before_birthday = ? or b.days_before_next_year_birthday = ? " +
                " order by b.{0} ";

        String realQuery = MessageFormat.format(query,
                                                UsersColumns.ID,
                                                UsersColumns.NAME,
                                                UsersColumns.EMAIL,
                                                UsersColumns.BIRTHDAY,
                                                UsersRepository.TABLE_NAME,
                                                UsersColumns.AVATAR);

        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), realQuery)) {
            logger.trace(realQuery);
            ps.bindParameters(inNbDays, inNbDays);

            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name()),
                                       res.getInt("days_before_birthday") < 0 ? res.getInt(
                                               "days_before_next_year_birthday")
                                               : res.getInt("days_before_birthday")));
                }
            }
        }

        return users;
    }

    /**
     * @param first  One user id.
     * @param second Another user id.
     * @return True if and only if the two guys are friends. False for the owner.
     */
    public static boolean associationExists(int first, int second) {
        String query = MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?",
                                            TABLE_NAME,
                                            UserRelationsColumns.FIRST_USER,
                                            UserRelationsColumns.SECOND_USER);
        return getDb().doesReturnRows(query, first, second);
    }

    /**
     * Creates a new friendship.
     *
     * @param userThatSendTheRequest    The user that is asking to be friend.
     * @param userThatReceiveTheRequest The user that is asked to be friend.
     */
    public static void addAssociation(int userThatSendTheRequest, int userThatReceiveTheRequest) throws SQLException {
        getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?,?,now())",
                                                               TABLE_NAME,
                                                               UserRelationsColumns.FIRST_USER,
                                                               UserRelationsColumns.SECOND_USER,
                                                               UserRelationsColumns.RELATION_DATE),
                                          userThatSendTheRequest,
                                          userThatReceiveTheRequest);
        getDb().executeUpdateGeneratedKey(MessageFormat.format("insert into {0} ({1},{2},{3}) values (?,?,now())",
                                                               TABLE_NAME,
                                                               UserRelationsColumns.FIRST_USER,
                                                               UserRelationsColumns.SECOND_USER,
                                                               UserRelationsColumns.RELATION_DATE),
                                          userThatReceiveTheRequest,
                                          userThatSendTheRequest);
    }

    /**
     * Drops a friendship.
     *
     * @param firstUserId  One user id.
     * @param secondUserId Another user id.
     */
    public static void deleteAssociation(int firstUserId, int secondUserId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format(
                "delete from {0} where ({1} = ? and {2} = ?) or ({1} = ? and {2} = ?)",
                TABLE_NAME,
                UserRelationsColumns.FIRST_USER,
                UserRelationsColumns.SECOND_USER),
                              firstUserId,
                              secondUserId,
                              secondUserId,
                              firstUserId);
    }

    public static void removeAllAssociationsTo(int userId) throws SQLException {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ? or {2} = ?",
                                                   TABLE_NAME,
                                                   UserRelationsColumns.FIRST_USER,
                                                   UserRelationsColumns.SECOND_USER),
                              userId,
                              userId);
    }

    /**
     * @param userId          The user id.
     * @param userNameOrEmail The user or email to look for. Allow one character mistake.
     * @param firstRow        The first row to fetch.
     * @param limit           Maximum number of results.
     * @return The list of users.
     */
    public static List<User> getAllNamesOrEmailsInRelation(int userId,
                                                           String userNameOrEmail,
                                                           int firstRow,
                                                           int limit) throws SQLException {

        List<User> users = new ArrayList<>();
        userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
        int length = userNameOrEmail.length() - StringUtils.countMatches(userNameOrEmail, "!") - 2;
        PreparedStatementIdKdo ps = null;

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("select res.{0}, res.{1}, res.{2}, res.{3}, res.{4} \n ",
                                          UsersColumns.ID,
                                          UsersColumns.NAME,
                                          UsersColumns.EMAIL,
                                          UsersColumns.BIRTHDAY,
                                          UsersColumns.AVATAR));
        query.append("  from ( \n ");

        query.append(MessageFormat.format("select 10 as pertinence, u.{0}, u.{1}, u.{2}, u.{3}, u.{4} \n ",
                                          UsersColumns.ID,
                                          UsersColumns.NAME,
                                          UsersColumns.EMAIL,
                                          UsersColumns.BIRTHDAY,
                                          UsersColumns.AVATAR));
        query.append(MessageFormat.format("  from {0} u, {1} r \n ", UsersRepository.TABLE_NAME, TABLE_NAME));
        query.append(MessageFormat.format(" where u.{0} = r.{1} \n ",
                                          UsersColumns.ID,
                                          UserRelationsColumns.FIRST_USER));
        query.append(MessageFormat.format("   and r.{0} = ? \n ", UserRelationsColumns.SECOND_USER));
        query.append(MessageFormat.format("   and ( lower(u.{0}) like ? ESCAPE ''!'' \n ", UsersColumns.NAME));
        query.append(MessageFormat.format(" or lower(u.{0}) like ? ESCAPE ''!'') \n ", UsersColumns.EMAIL));

        // une erreur
        if (length > 0) {
            query.append(" union \n ");

            query.append(MessageFormat.format("select 2 as pertinence, u.{0}, u.{1}, u.{2}, u.{3}, u.{4} \n ",
                                              UsersColumns.ID,
                                              UsersColumns.NAME,
                                              UsersColumns.EMAIL,
                                              UsersColumns.BIRTHDAY,
                                              UsersColumns.AVATAR));
            query.append(MessageFormat.format("  from {0} u, {1} r \n ", UsersRepository.TABLE_NAME, TABLE_NAME));
            query.append(MessageFormat.format(" where u.{0} = r.{1} \n ",
                                              UsersColumns.ID,
                                              UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("   and r.{0} = ? \n ", UserRelationsColumns.SECOND_USER));
            query.append("   and ( \n ");
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    query.append(" or ");
                }
                query.append(MessageFormat.format("    lower(u.{0}) like ? ESCAPE ''!'' \n ", UsersColumns.NAME));
                query.append(MessageFormat.format(" or lower(u.{0}) like ? ESCAPE ''!'' \n ", UsersColumns.EMAIL));
            }
            query.append("                        )\n ");
        }

        // deux erreurs
        if (length > 1) {
            query.append(" union \n ");

            query.append(MessageFormat.format("select 1 as pertinence, u.{0}, u.{1}, u.{2}, u.{3}, u.{4} \n ",
                                              UsersColumns.ID,
                                              UsersColumns.NAME,
                                              UsersColumns.EMAIL,
                                              UsersColumns.BIRTHDAY,
                                              UsersColumns.AVATAR));
            query.append(MessageFormat.format("  from {0} u, {1} r \n ", UsersRepository.TABLE_NAME, TABLE_NAME));
            query.append(MessageFormat.format(" where u.{0} = r.{1} \n ",
                                              UsersColumns.ID,
                                              UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("   and r.{0} = ? \n ", UserRelationsColumns.SECOND_USER));
            query.append("   and ( \n ");
            for (int i = 0; i < length - 1; i++) {
                if (i > 0) {
                    query.append(" or ");
                }
                query.append(MessageFormat.format("    lower(u.{0}) like ? ESCAPE ''!'' \n ", UsersColumns.NAME));
                query.append(MessageFormat.format(" or lower(u.{0}) like ? ESCAPE ''!'' \n ", UsersColumns.EMAIL));
            }
            query.append("                        )\n ");
        }

        query.append("       ) res \n ");
        query.append(MessageFormat.format(" group by res.{0}, res.{1}, res.{2}, res.{3}, res.{4} \n ",
                                          UsersColumns.ID,
                                          UsersColumns.NAME,
                                          UsersColumns.EMAIL,
                                          UsersColumns.BIRTHDAY,
                                          UsersColumns.AVATAR));
        query.append(" order by sum(pertinence) desc, 2, 3 \n ");
        query.append(" LIMIT ?, ? \n ");
        logger.trace(query.toString());

        try {
            ps = new PreparedStatementIdKdo(getDb(), query.toString());
            int size = length > 1 ? 2 * length + 7 + 2 * (length - 1) : 8;
            if (length == 0) size = 5;
            Object[] parameters = new Object[size];
            parameters[0] = userId;
            parameters[1] = userNameOrEmail;
            parameters[2] = userNameOrEmail;
            if (length > 0) {
                parameters[3] = userId;
            }

            // une erreur => 2*length elements
            for (int i = 1; i < userNameOrEmail.length() - 1; i++) {
                if ('!' == userNameOrEmail.charAt(i)) {
                    continue;
                }
                String toBeSearched = MessageFormat.format("{0}_{1}",
                                                           userNameOrEmail.substring(0, i),
                                                           userNameOrEmail.substring(i + 1));
                parameters[2 * (i - 1) + 4] = toBeSearched; // for names
                parameters[2 * (i - 1) + 5] = toBeSearched; // for emails
            }
            if (length > 1) { // deux erreurs => 2 * (length - 1) elements
                parameters[2 * length + 4] = userId;
                for (int i = 1; i < userNameOrEmail.length() - 2; i++) {
                    if ('!' == userNameOrEmail.charAt(i) || '!' == userNameOrEmail.charAt(i + 1)) {
                        continue;
                    }
                    String toBeSearched = MessageFormat.format("{0}__{1}",
                                                               userNameOrEmail.substring(0, i),
                                                               userNameOrEmail.substring(i + 2));
                    parameters[2 * length + 2 * (i - 1) + 5] = toBeSearched; // for names
                    parameters[2 * length + 2 * (i - 1) + 6] = toBeSearched; // for emails
                }
            }
            parameters[parameters.length - 2] = firstRow;
            parameters[parameters.length - 1] = limit;
            logger.trace(MessageFormat.format("Parameters: {0}", Arrays.toString(parameters)));
            ps.bindParameters(parameters);

            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return users;
    }

    /**
     * @param suggestedBy     The user id that suggests the relation.
     * @param suggestedTo     The user id that receives the suggestion.
     * @param userNameOrEmail The token.
     * @param firstRow        Index of the first row.
     * @param limit           Maximal size of the result.
     * @return All users matching the name/email that are in suggestedBy network, but not in suggestedTo network.
     */
    public static List<User> getAllUsersInRelationNotInOtherNetwork(int suggestedBy,
                                                                    int suggestedTo,
                                                                    String userNameOrEmail,
                                                                    int firstRow,
                                                                    int limit) throws SQLException {

        List<User> users = new ArrayList<>();
        PreparedStatementIdKdo ps = null;
        logger.debug(suggestedBy + " / " + suggestedTo + " / " + userNameOrEmail);

        StringBuilder query = new StringBuilder();
        query.append("select u.{0}, u.{1}, u.{2}, u.{7}, u.{8} ");
        query.append("  from {3} u, {4} r ");
        query.append(" where u.{0} = r.{6} and r.{5} = ? ");
        query.append("   and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
        query.append("   and not exists ");
        query.append("       ( ");
        query.append("          select 1 ");
        query.append("            from {4} r2 ");
        query.append("            join {3} u2 ");
        query.append("              on u2.{0} = r2.{6} and r2.{5} = ? ");
        query.append("           where (lower(u2.{1}) like ? ESCAPE ''!'' or lower(u2.{2}) like ? ESCAPE ''!'') ");
        query.append("             and r.{6} = r2.{6} ");
        query.append("       ) ");
        query.append(" order by {1}, {2}, {0} ");
        query.append(" LIMIT ?, ? ");

        try {
            String formatedQuery = MessageFormat.format(query.toString(),
                                                        UsersColumns.ID.name(),
                                                        UsersColumns.NAME.name(),
                                                        UsersColumns.EMAIL.name(),
                                                        UsersRepository.TABLE_NAME,
                                                        TABLE_NAME,
                                                        UserRelationsColumns.FIRST_USER,
                                                        UserRelationsColumns.SECOND_USER,
                                                        UsersColumns.AVATAR,
                                                        UsersColumns.BIRTHDAY);

            ps = new PreparedStatementIdKdo(getDb(), formatedQuery);
            userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
            ps.bindParameters(suggestedBy,
                              userNameOrEmail,
                              userNameOrEmail,
                              suggestedTo,
                              userNameOrEmail,
                              userNameOrEmail,
                              firstRow,
                              limit);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return users;
    }

    public static List<User> getAllUsersInRelation(int userId,
                                                   String userNameOrEmail,
                                                   int firstRow,
                                                   int limit) throws SQLException {

        List<User> users = new ArrayList<>();
        PreparedStatementIdKdo ps = null;

        StringBuilder query = new StringBuilder();
        query.append("select u.{0}, u.{1}, u.{2}, u.{7}, u.{8} ");
        query.append("from {3} u, {4} r ");
        query.append("where u.{0} = r.{6} and r.{5} = ? ");
        query.append("  and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
        query.append("order by {1}, {2}, {0} ");
        query.append(" LIMIT ?, ? ");

        try {
            ps = new PreparedStatementIdKdo(getDb(),
                                            MessageFormat.format(query.toString(),
                                                                 UsersColumns.ID.name(),
                                                                 UsersColumns.NAME.name(),
                                                                 UsersColumns.EMAIL.name(),
                                                                 UsersRepository.TABLE_NAME,
                                                                 TABLE_NAME,
                                                                 UserRelationsColumns.FIRST_USER,
                                                                 UserRelationsColumns.SECOND_USER,
                                                                 UsersColumns.AVATAR,
                                                                 UsersColumns.BIRTHDAY));
            userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
            ps.bindParameters(userId, userNameOrEmail, userNameOrEmail, firstRow, limit);
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return users;
    }

    /**
     * @param user            The user.
     * @param userNameOrEmail The token.
     * @return The number of users belonging to userId network and matching name/email
     */
    public static int getAllUsersInRelationCount(User user, String userNameOrEmail) {

        StringBuilder query = new StringBuilder();
        query.append("select count(*) ");
        query.append("from {3} u, {4} r ");
        query.append("where u.{0} = r.{6} and r.{5} = ? ");

        if (userNameOrEmail != null && !userNameOrEmail.isEmpty()) {
            query.append("  and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
        }

        query.append("order by u.{1}, u.{2}, u.{0}");

        String formatQuery = MessageFormat.format(query.toString(),
                                                  UsersColumns.ID.name(),
                                                  UsersColumns.NAME.name(),
                                                  UsersColumns.EMAIL.name(),
                                                  UsersRepository.TABLE_NAME,
                                                  TABLE_NAME,
                                                  UserRelationsColumns.FIRST_USER,
                                                  UserRelationsColumns.SECOND_USER);
        logger.trace(formatQuery);

        if (userNameOrEmail != null && !userNameOrEmail.isEmpty()) {
            userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
            return getDb().selectCountStar(formatQuery, user.id, userNameOrEmail, userNameOrEmail);
        } else {
            return getDb().selectCountStar(formatQuery, user.id);
        }
    }

    /**
     * @param user The user.
     * @return All user friends, without him.
     */
    public static List<User> getAllUsersInRelation(User user) {
        return getAllUsersInRelation(user, -1, -1);
    }

    public static List<User> getAllUsersInRelation(User user, int firstRow, int limit) {

        List<User> users = new ArrayList<>();
        PreparedStatementIdKdo ps = null;

        StringBuilder query = new StringBuilder();
        query.append("select u.{0}, u.{1}, u.{2}, u.{7}, u.{8} ");
        query.append("from {3} u, {4} r ");
        query.append("where u.{0} = r.{6} and r.{5} = ? ");
        query.append("order by {1}, {2}, {0}");
        if (firstRow > -1 && limit > 0) {
            query.append(" LIMIT ?, ? ");
        }

        try {
            String q = MessageFormat.format(query.toString(),
                                            UsersColumns.ID.name(),
                                            UsersColumns.NAME.name(),
                                            UsersColumns.EMAIL.name(),
                                            UsersRepository.TABLE_NAME,
                                            TABLE_NAME,
                                            UserRelationsColumns.FIRST_USER,
                                            UserRelationsColumns.SECOND_USER,
                                            UsersColumns.AVATAR,
                                            UsersColumns.BIRTHDAY);
            logger.trace(q);
            ps = new PreparedStatementIdKdo(getDb(), q);
            if (firstRow > -1 && limit > 0) {
                ps.bindParameters(user.id, firstRow, limit);
            } else {
                ps.bindParameters(user.id);
            }
            if (ps.execute()) {
                ResultSet res = ps.getResultSet();
                while (res.next()) {
                    users.add(new User(res.getInt(UsersColumns.ID.name()),
                                       res.getString(UsersColumns.NAME.name()),
                                       res.getString(UsersColumns.EMAIL.name()),
                                       res.getDate(UsersColumns.BIRTHDAY.name()),
                                       res.getString(UsersColumns.AVATAR.name())));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return users;
    }
}
