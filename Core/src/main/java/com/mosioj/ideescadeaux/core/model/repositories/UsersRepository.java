package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserRole;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRelationsColumns;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UsersColumns;
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
        Query<User> query = session.createQuery("FROM USERS WHERE email = :email ", User.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    /**
     * @param id The user's id.
     * @return The user corresponding to this ID or null if not found.
     */
    public static Optional<User> getUser(Integer id) {
        return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.get(User.class, id)));
    }

    /**
     * @param nameOrEmail The name or email of the person.
     * @return All the matching persons.
     */
    public static List<User> getUserFromNameOrEmail(String nameOrEmail) {
        return HibernateUtil.doQueryFetch(s -> {
            Query<User> query = s.createQuery("from USERS where email = :nameOrEmail or name = :nameOrEmail",
                                              User.class);
            query.setParameter("nameOrEmail", nameOrEmail);
            return query.list();
        });
    }

    /**
     * @return All the users in DB. Only used for administration
     */
    public static List<User> getAllUsers() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("FROM USERS ORDER BY creation_date DESC", User.class)
                                                .list());
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
                                      int limit) throws SQLException {

        logger.debug(MessageFormat.format("Getting users from search token: ''{0}'' for user {1}.",
                                          pNameToMatch,
                                          userIdToSkip));

        final String nameToMatch = HibernateUtil.sanitizeSQLLike(pNameToMatch);

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format("  from {0} u ", TABLE_NAME));
        query.append(MessageFormat.format(" where (lower({0}) like :pNameToMatch ESCAPE ''!''   ", UsersColumns.NAME));
        query.append(MessageFormat.format("    or  lower({0}) like :pNameToMatch ESCAPE ''!'' ) ", UsersColumns.EMAIL));
        query.append("   and id <> :userIdToSkip ");
        if (selectOnlyNonFriends) {
            query.append("   and not exists ( ");
            query.append(MessageFormat.format(" select 1 from {0}  ", UserRelationsRepository.TABLE_NAME));
            query.append(MessageFormat.format("  where {0} = :userIdToSkip ", UserRelationsColumns.FIRST_USER));
            query.append(MessageFormat.format("    and {0} = u.id ", UserRelationsColumns.SECOND_USER));
            query.append("   ) ");
        }
        query.append(MessageFormat.format(" order by {0}, {1}, {2} ",
                                          UsersColumns.NAME,
                                          UsersColumns.EMAIL,
                                          UsersColumns.ID));

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

        StringBuilder queryText = new StringBuilder();
        queryText.append("select count(*)");
        queryText.append("  from USERS u ");
        queryText.append(MessageFormat.format(" where ({0} like :name ESCAPE ''!''   ", UsersColumns.NAME));
        queryText.append(MessageFormat.format("    or  {0} like :name ESCAPE ''!'' ) ", UsersColumns.EMAIL));
        queryText.append(MessageFormat.format("   and {0} <> :id ", UsersColumns.ID));
        if (selectOnlyNonFriends) {
            queryText.append("   and not exists ( ");
            queryText.append(MessageFormat.format(" select 1 from {0}  ", UserRelationsRepository.TABLE_NAME));
            queryText.append(MessageFormat.format("  where {0} = :id   ", UserRelationsColumns.FIRST_USER));
            queryText.append(MessageFormat.format("    and {0} = u.id ", UserRelationsColumns.SECOND_USER));
            queryText.append("   ) ");
        }

        return HibernateUtil.doQuerySingle(s -> {
            Query<Long> query = s.createQuery(queryText.toString(), Long.class);
            query.setParameter("name", theNameToMatch);
            query.setParameter("id", userIdToSkip);
            return query.getSingleResult();
        });
    }

    public static void deleteUser(User user) throws SQLException {
        logger.info(MessageFormat.format("Suppression de {0}...", user));
        int userId = user.id;

        // Suppression des commentaires
        CommentsRepository.deleteAll(userId);

        // Suppression des participations aux groupes
        GroupIdeaRepository.removeUserFromAllGroups(user);

        // Suppression des idées
        // Suppression de l'historique des idées
        IdeesRepository.removeAll(userId);

        // Suppression des notifications
        NotificationsRepository.terminator().whereOwner(user).terminates();

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

        // Et !! Suppression du user
        HibernateUtil.doSomeWork(s -> {
            // On ne peut pas se baser sur le CASCADE... Il faudrait être sûr que tous les objets soient chargés
            Transaction t = s.beginTransaction();
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
