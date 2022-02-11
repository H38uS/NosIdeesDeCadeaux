package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.Relation;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserRelationsRepository {

    private static final Logger logger = LogManager.getLogger(UserRelationsRepository.class);

    private UserRelationsRepository() {
        // Forbidden
    }

    /**
     * @param user        The user.
     * @param inNbDaysMax The number of days to select the birthday.
     * @return The list of users with birthday coming (less than 30 days).
     */
    public static List<User> getCloseBirthday(User user, int inNbDaysMax) {
        return getAllUsersInRelation(user).stream()
                                          .filter(u -> u.nbDaysBeforeBirthday <= inNbDaysMax)
                                          .collect(Collectors.toList());
    }

    /**
     * @param inNbDays Number of days before the birthday.
     * @return The list of users with birthday coming (less than 30 days).
     */
    public static List<User> getBirthday(int inNbDays) {
        return UsersRepository.getAllUsers()
                              .stream()
                              .filter(u -> u.nbDaysBeforeBirthday == inNbDays)
                              .collect(Collectors.toList());
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

        final String sanitizedToken = HibernateUtil.sanitizeSQLLike(userNameOrEmail);
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

        final String theUserNameOrEmail = HibernateUtil.sanitizeSQLLike(userNameOrEmail);
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
                query.setParameter("nameOrEmail", HibernateUtil.sanitizeSQLLike(nameOrEmail));
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
            Query<Long> query = s.createQuery(queryText.toString(), Long.class).setParameter("id", user);
            if (shouldFilterWithNameOrEmail) {
                query.setParameter("nameOrEmail", HibernateUtil.sanitizeSQLLike(nameOrEmail));
            }
            return query.uniqueResult().intValue();
        });
    }
}
