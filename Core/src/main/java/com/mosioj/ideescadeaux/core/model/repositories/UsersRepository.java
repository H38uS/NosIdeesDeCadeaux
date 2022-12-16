package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserRole;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

/**
 * Représente la table de personnes.
 *
 * @author Jordan Mosio
 */
public class UsersRepository {

    public static final String TABLE_NAME = "USERS";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class);

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
    public static int addNewPersonne(final String email, final String digestedPwd, final String name) {
        return HibernateUtil.doQueryOptional(s -> {
            Transaction t = s.beginTransaction();
            final User user = new User(email, name, digestedPwd);
            Integer userId = (Integer) s.save(user);
            s.save(new UserRole(user, UserRole.RoleName.ROLE_USER));
            t.commit();
            return Optional.of(userId);
        }).orElse(-1);
    }

    /**
     * @param email The user's email.
     * @return The corresponding user, if it exists.
     */
    public static Optional<User> getUser(String email) {
        return HibernateUtil.doQueryOptional(s -> getUser(email, s));
    }

    /**
     * @param email   The user's email.
     * @param session The hibernate session.
     * @return The corresponding user, if it exists.
     */
    public static Optional<User> getUser(String email, Session session) {
        final String queryTest = "select u " +
                                 "FROM USERS u " +
                                 "  left join fetch u.roles " +
                                 " WHERE u.email = :email ";
        Query<User> query = session.createQuery(queryTest, User.class)
                                   .setParameter("email", email);
        return query.uniqueResultOptional();
    }

    /**
     * @param id The user's id.
     * @return The user corresponding to this ID or null if not found.
     */
    public static Optional<User> getUser(Integer id) {
        return HibernateUtil.doQueryOptional(s -> {
            final String queryTest = "select u " +
                                     "FROM USERS u " +
                                     "  left join fetch u.roles " +
                                     " WHERE u.id = :id ";
            return s.createQuery(queryTest, User.class).setParameter("id", id).uniqueResultOptional();
        });
    }

    /**
     * @param nameOrEmail The name or email of the person.
     * @return All the matching persons.
     */
    public static List<User> getUserFromNameOrEmail(String nameOrEmail) {
        return HibernateUtil.doQueryFetch(s -> {
            final String queryText = "select u " +
                                     "  from USERS u " +
                                     "  left join fetch u.roles " +
                                     " where u.email = :nameOrEmail or u.name = :nameOrEmail";
            Query<User> query = s.createQuery(queryText, User.class).setParameter("nameOrEmail", nameOrEmail);
            return query.list();
        });
    }

    /**
     * @return All the ADMIN users in DB.
     */
    public static List<User> getAllAdmins() {
        final String queryText = "select u " +
                                 "  FROM USERS u " +
                                 "  left join fetch u.roles r " +
                                 " where r.role = 'ROLE_ADMIN'" +
                                 " ORDER BY u.creationDate DESC";
        return HibernateUtil.doQueryFetch(s -> s.createQuery(queryText, User.class).list());
    }

    /**
     * @return All the users in DB. Only used for administration
     */
    public static List<User> getAllUsers() {
        final String queryText = "select u " +
                                 "  FROM USERS u " +
                                 "  left join fetch u.roles " +
                                 " ORDER BY u.creationDate DESC";
        return HibernateUtil.doQueryFetch(s -> s.createQuery(queryText, User.class).list());
    }

    /**
     * Excludes the current user.
     *
     * @param pNameToMatch         The name or email.
     * @param userIdToSkip         The id not to select.
     * @param selectOnlyNonFriends True to select only non friends.
     * @param firstRow             The first index.
     * @param limit                Maximum result size.
     * @return The list of users.
     */
    public static List<User> getUsers(String pNameToMatch,
                                      int userIdToSkip,
                                      boolean selectOnlyNonFriends,
                                      int firstRow,
                                      int limit) {

        logger.debug(MessageFormat.format("Getting users from search token: ''{0}'' for user {1}.",
                                          pNameToMatch,
                                          userIdToSkip));

        final String nameToMatch = HibernateUtil.sanitizeSQLLike(pNameToMatch);

        StringBuilder query = new StringBuilder();
        query.append("select u ");
        query.append("  from USERS u ");
        query.append("  left join fetch u.roles ");
        query.append(" where (lower(u.name) like :pNameToMatch ESCAPE '!' ");
        query.append("    or  lower(u.email) like :pNameToMatch ESCAPE '!' ) ");
        query.append("   and u.id <> :userIdToSkip ");
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(" select 1 from USER_RELATIONS  ");
            query.append("  where first_user = :userIdToSkip ");
            query.append("    and second_user = u.id ");
            query.append("   ) ");
        }
        query.append(" order by u.name, u.email, u.id ");

        return HibernateUtil.doQueryFetch(s -> s.createQuery(query.toString(), User.class)
                                                .setParameter("pNameToMatch", nameToMatch)
                                                .setParameter("userIdToSkip", userIdToSkip)
                                                .setFirstResult(firstRow)
                                                .setMaxResults(limit)
                                                .list());
    }

    /**
     * Excludes the current user.
     *
     * @param nameToMatch          Name or email.
     * @param userIdToSkip         The id not to select.
     * @param selectOnlyNonFriends True to select only non friends.
     * @return The number of users matching this name/email.
     */
    public static long getTotalUsers(String nameToMatch, int userIdToSkip, boolean selectOnlyNonFriends) {

        final String theNameToMatch = HibernateUtil.sanitizeSQLLike(nameToMatch);

        String queryText = """
                    select count(*)
                      from USERS u
                     where (name  like :name ESCAPE ''!'' or  email like :name ESCAPE ''!'' )
                       and id <> :id
                """;
        if (selectOnlyNonFriends) {
            queryText += """
                       and not exists (
                         select 1
                           from USER_RELATIONS
                          where first_user = :id
                            and second_user = u.id
                       )
                    """;
        }

        final String fullQuery = queryText;
        return HibernateUtil.doQuerySingle(s -> {
            Query<Long> query = s.createQuery(fullQuery, Long.class);
            query.setParameter("name", theNameToMatch);
            query.setParameter("id", userIdToSkip);
            return query.getSingleResult();
        });
    }

    public static void deleteUser(User user) throws SQLException {
        logger.info(MessageFormat.format("Suppression de {0}...", user));
        final int userId = user.id;

        // Suppression des commentaires
        CommentsRepository.deleteAll(user);

        // Suppression des notifications
        NotificationsRepository.terminator().whereOwner(user).terminates();

        // Suppression des relations parents - enfants
        ParentRelationshipRepository.deleteAllRelationForUser(userId);

        // Suppresion des questions
        QuestionsRepository.deleteAll(user);

        // Suppression des demandes de modifications de mdp
        UserChangePwdRequestRepository.deleteAssociation(userId);

        // Suppression des paramètres utilisateurs
        UserParametersRepository.deleteAllUserParameters(userId);

        // Et !! Suppression de l'utilisateur
        HibernateUtil.doSomeWork(s -> {
            // On ne peut pas se baser sur le CASCADE... Il faudrait être sûr que tous les objets soient chargés
            Transaction t = s.beginTransaction();

            // Suppression des idées
            // Suppression de l'historique des idées
            s.createQuery("delete from IDEES where owner = :owner")
             .setParameter("owner", user)
             .executeUpdate();

            // Suppression des relations, des suggestions et des demandes
            s.createQuery(
                     "delete from USER_RELATIONS_SUGGESTION where suggested_by = :id or suggested_to = :id or user_id = :id")
             .setParameter("id", userId)
             .executeUpdate();
            s.createQuery("delete from USER_RELATION_REQUESTS where sent_by = :by or sent_to = :to")
             .setParameter("by", user)
             .setParameter("to", user)
             .executeUpdate();
            s.createQuery("delete from USER_RELATIONS where first_user = :user or second_user = :user")
             .setParameter("user", user)
             .executeUpdate();

            // Suppression de toutes les participations aux groupes
            s.createQuery("delete from IdeaGroupContent where user_id = :user")
             .setParameter("user", user)
             .executeUpdate();

            // Suppression des groupes vides
            GroupIdeaRepository.getAllGroups(s)
                               .stream()
                               .filter(g -> g.getShares().isEmpty())
                               .toList()
                               .forEach(g -> s.createQuery("Delete from GROUP_IDEA where id = :id")
                                              .setParameter("id", g.getId())
                                              .executeUpdate());

            // Les roles
            s.createQuery("delete from USER_ROLES where email = :email")
             .setParameter("email", user.email)
             .executeUpdate();
            // Et le user !
            getUser(user.email, s).ifPresent(s::delete);
            t.commit();
        });
    }

    /**
     * Lorsque l'on vient de se connecter.
     *
     * @param email The user's email.
     */
    public static void touch(String email) throws SQLException {
        getUser(email).ifPresent(u -> {
            u.touchLastLogin();
            HibernateUtil.update(u);
        });
    }

}
