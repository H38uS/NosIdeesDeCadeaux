package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.entities.Relation;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class UserRelationsRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USER_RELATIONS";
    private static final Logger logger = LogManager.getLogger(UserRelationsRepository.class);

    private UserRelationsRepository() {
        // Forbidden
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
        query.append("select b.{0}, b.{1}, b.{2}, b.{3}, b.{8} ");
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
     * @param inNbDays Number of days before the birthday.
     * @return The list of users with birthday coming (less than 30 days).
     */
    public static List<User> getBirthday(int inNbDays) throws SQLException {

        List<User> users = new ArrayList<>();

        String query =
                "select b.{0}, b.{1}, b.{2}, b.{3}, b.{5} " +
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
                                       res.getString(UsersColumns.AVATAR.name())));
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
    public static boolean associationExists(User first, User second) {
        final String query = "select 1 from USER_RELATIONS where first_user = ?0 and second_user = ?1";
        return HibernateUtil.doesReturnRows(query, first, second);
    }

    /**
     * Creates a new friendship.
     *
     * @param userThatSendTheRequest    The user that is asking to be friend.
     * @param userThatReceiveTheRequest The user that is asked to be friend.
     */
    public static void addAssociation(User userThatSendTheRequest, User userThatReceiveTheRequest) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.save(new Relation(userThatSendTheRequest, userThatReceiveTheRequest));
            s.save(new Relation(userThatReceiveTheRequest, userThatSendTheRequest));
            t.commit();
        });
    }

    /**
     * Drops a friendship.
     *
     * @param firstUser  One user id.
     * @param secondUser Another user id.
     */
    public static void deleteAssociation(User firstUser, User secondUser) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            final String queryString =
                    "delete from USER_RELATIONS " +
                    " where (first_user = :first and second_user = :second) " +
                    "    or (first_user = :second and second_user = :first)";
            s.createQuery(queryString)
             .setParameter("first", firstUser)
             .setParameter("second", secondUser)
             .executeUpdate();
            t.commit();
        });
    }

    public static void removeAllAssociationsTo(Session s, User user) {
        s.createQuery("delete from USER_RELATIONS where first_user = :user or second_user = :user")
         .setParameter("user", user)
         .executeUpdate();
    }

    /**
     * @param length The length of the token to match.
     * @return The query String to catch errors.
     */
    private static String prepareErrorQuery(int length) {
        final StringBuilder one_error_query = new StringBuilder(
                " select u " +
                "   from USERS u, USER_RELATIONS r " +
                "  where u.id = r.first" +
                "    and r.second = ?0" +
                "    and ( ");

        int paramIndex = 1;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                one_error_query.append(" or ");
            }
            one_error_query.append("    lower(name)  like ?").append(paramIndex++).append(" ESCAPE '!' ");
            one_error_query.append(" or lower(email) like ?").append(paramIndex++).append(" ESCAPE '!' ");
        }
        one_error_query.append(
                "              ) " +
                "  order by u.name, u.email, u.id ");
        return one_error_query.toString();
    }

    /**
     * @param user            The user.
     * @param userNameOrEmail The user or email to look for. Allow one character mistake.
     * @param firstRow        The first row to fetch.
     * @param limit           Maximum number of results.
     * @return The list of users.
     */
    public static List<User> getAllUsersInRelationWithPossibleTypo(User user,
                                                                   String userNameOrEmail,
                                                                   int firstRow,
                                                                   int limit) {

        final String sanitizedToken = sanitizeSQLLike(userNameOrEmail);
        int length = sanitizedToken.length() - StringUtils.countMatches(sanitizedToken, "!") - 2;

        // Exact matches
        final String exact_query =
                " select u " +
                "   from USERS u, USER_RELATIONS r " +
                "  where u.id = r.first" +
                "    and r.second = :user " +
                "    and (lower(name)  like :nameOrEmail ESCAPE '!' " +
                "         or " +
                "         lower(email) like :nameOrEmail ESCAPE '!')" +
                "  order by u.name, u.email, u.id ";

        LinkedHashSet<User> result = new LinkedHashSet<>(HibernateUtil.doQueryFetch(s -> {

            Query<User> query = s.createQuery(exact_query, User.class);
            query.setParameter("user", user);
            query.setParameter("nameOrEmail", sanitizedToken);

            query.setFirstResult(firstRow);
            query.setMaxResults(limit);
            return query.list();
        }));

        // une erreur de typo dans la recherche
        if (length > 0) {
            final String one_error_query = prepareErrorQuery(length);
            result.addAll(HibernateUtil.doQueryFetch(s -> {
                Query<User> query = s.createQuery(one_error_query, User.class);
                Object[] parameters = new Object[2 * length];
                for (int i = 1; i < sanitizedToken.length() - 1; i++) {
                    if ('!' == sanitizedToken.charAt(i)) {
                        continue;
                    }
                    String toBeSearched = MessageFormat.format("{0}_{1}",
                                                               sanitizedToken.substring(0, i),
                                                               sanitizedToken.substring(i + 1));
                    parameters[2 * (i - 1)] = toBeSearched; // for names
                    parameters[2 * (i - 1) + 1] = toBeSearched; // for emails
                }
                query.setParameter(0, user);
                for (int i = 0; i < parameters.length; i++) {
                    query.setParameter(i + 1, parameters[i]);
                }
                query.setFirstResult(firstRow);
                query.setMaxResults(limit);
                return query.list();
            }));
        }

        // deux erreurs de typo dans la recherche
        if (length > 1) {
            final String two_errors_query = prepareErrorQuery(length);
            result.addAll(HibernateUtil.doQueryFetch(s -> {
                Query<User> query = s.createQuery(two_errors_query, User.class);
                Object[] parameters = new Object[2 * length];
                for (int i = 1; i < sanitizedToken.length() - 2; i++) {
                    if ('!' == sanitizedToken.charAt(i) || '!' == sanitizedToken.charAt(i + 1)) {
                        continue;
                    }
                    String toBeSearched = MessageFormat.format("{0}__{1}",
                                                               sanitizedToken.substring(0, i),
                                                               sanitizedToken.substring(i + 2));
                    parameters[2 * (i - 1)] = toBeSearched; // for names
                    parameters[2 * (i - 1) + 1] = toBeSearched; // for emails
                }
                query.setParameter(0, user);
                for (int i = 0; i < parameters.length; i++) {
                    query.setParameter(i + 1, parameters[i]);
                }
                query.setFirstResult(firstRow);
                query.setMaxResults(limit);
                return query.list();
            }));
        }
        return new ArrayList<>(result);
    }

    /**
     * @param suggestedBy     The user id that suggests the relation.
     * @param suggestedTo     The user id that receives the suggestion.
     * @param userNameOrEmail The token.
     * @param firstRow        Index of the first row.
     * @param limit           Maximal size of the result.
     * @return All users matching the name/email that are in suggestedBy network, but not in suggestedTo network.
     */
    public static List<User> getAllUsersInRelationNotInOtherNetwork(User suggestedBy,
                                                                    User suggestedTo,
                                                                    String userNameOrEmail,
                                                                    int firstRow,
                                                                    int limit) {

        logger.debug(suggestedBy + " / " + suggestedTo + " / " + userNameOrEmail);

        String query = "select u" +
                       "  from USERS u, USER_RELATIONS r " +
                       " where u.id = r.second and r.first = :suggestedBy " +
                       "   and (lower(u.name) like :userNameOrEmail ESCAPE '!' or lower(u.email) like :userNameOrEmail ESCAPE '!') " +
                       "   and not exists " +
                       "       ( " +
                       "          select 1 " +
                       "            from USER_RELATIONS r2 " +
                       "            join USERS u2 " +
                       "              on u2.id = r2.second and r2.first = :suggestedTo " +
                       "           where (lower(u2.name) like :userNameOrEmail ESCAPE '!' or lower(u2.email) like :userNameOrEmail ESCAPE '!') " +
                       "             and r.second = r2.second " +
                       "       ) " +
                       " order by u.name, u.email, u.id ";

        final String theUserNameOrEmail = sanitizeSQLLike(userNameOrEmail);
        return HibernateUtil.doQueryFetch(s -> s.createQuery(query, User.class)
                                                .setParameter("suggestedBy", suggestedBy)
                                                .setParameter("suggestedTo", suggestedTo)
                                                .setParameter("userNameOrEmail", theUserNameOrEmail)
                                                .setFirstResult(firstRow)
                                                .setMaxResults(limit).list());
    }

    /**
     * @param user        The user.
     * @param nameOrEmail The token.
     * @param firstRow    The first row to fetch.
     * @param limit       The maximum number of row to fetch.
     * @return The number of users belonging to userId network and matching name/email
     */
    public static List<User> getAllUsersInRelation(User user, String nameOrEmail, int firstRow, int limit) {

        final boolean shouldFilterWithNameOrEmail = !StringUtils.isBlank(nameOrEmail);
        StringBuilder queryText = new StringBuilder(
                "select u " +
                "  from USERS u, USER_RELATIONS r" +
                " where u.id = r.first " +
                "   and r.second = :id ");
        if (shouldFilterWithNameOrEmail) {
            queryText.append("and (lower(u.name) like :nameOrEmail ESCAPE '!' " +
                             " or lower(u.email) like :nameOrEmail ESCAPE '!') ");
        }
        queryText.append("order by u.name, u.email, u.id");
        return HibernateUtil.doQueryFetch(s -> {
            Query<User> query = s.createQuery(queryText.toString(), User.class).setParameter("id", user);
            if (shouldFilterWithNameOrEmail) {
                query.setParameter("nameOrEmail", sanitizeSQLLike(nameOrEmail));
            }
            if (firstRow > -1) {
                query.setFirstResult(firstRow);
            }
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.list();
        });
    }

    /**
     * @param user The user.
     * @return All user friends, without him.
     */
    public static List<User> getAllUsersInRelation(User user) {
        // Shortcut
        return getAllUsersInRelation(user, StringUtils.EMPTY, -1, -1);
    }

    /**
     * @param user        The user.
     * @param nameOrEmail The token.
     * @return The number of users belonging to userId network and matching name/email
     */
    public static int getAllUsersInRelationCount(User user, String nameOrEmail) {
        final boolean shouldFilterWithNameOrEmail = !StringUtils.isBlank(nameOrEmail);
        StringBuilder queryText = new StringBuilder(
                "select count(*) " +
                "  from USER_RELATIONS urr " +
                "  left join USERS u1 on u1.id = urr.first " +
                " where urr.second = :id");
        if (shouldFilterWithNameOrEmail) {
            queryText.append("and (lower(u1.name) like :nameOrEmail ESCAPE '!' " +
                             " or lower(u1.email) like :nameOrEmail ESCAPE '!')");
        }
        return HibernateUtil.doQuerySingle(s -> {
            Query<Integer> query = s.createQuery(queryText.toString(), Integer.class).setParameter("id", user);
            if (shouldFilterWithNameOrEmail) {
                query.setParameter("nameOrEmail", sanitizeSQLLike(nameOrEmail));
            }
            return query.uniqueResult();
        });
    }
}
