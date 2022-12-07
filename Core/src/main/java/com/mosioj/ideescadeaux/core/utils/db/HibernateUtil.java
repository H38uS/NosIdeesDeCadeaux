package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public class HibernateUtil {

    public static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("com.mosioj.ideescadeaux");

    private HibernateUtil() {
        // Nothing to do
    }

    // ================ Main class interface
    // ================ Takes care of closing the session after the work

    /**
     * Performs some work on a new brand session.
     *
     * @param operation Operations to perform.
     */
    public static void doSomeWork(HibernateSessionOperation operation) {
        try (Session session = HibernateUtil.getASession()) {
            operation.work(session);
        }
    }

    /**
     * Performs some work on a new brand session and returns its results.
     *
     * @param operation Operations to perform.
     */
    public static <T> T doSomeExecutionWork(HibernateSessionExecuteUpdate<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            Transaction t = session.beginTransaction();
            T result = operation.executeUpdate(session);
            t.commit();
            return result;
        }
    }

    /**
     * @param <T>       The type of result.
     * @param operation The operation to perform.
     * @return The optional row found.
     */
    public static <T> List<T> doQueryFetch(HibernateSessionQueryFetch<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            return operation.fetch(session);
        }
    }

    /**
     * @param <T>       The type of result.
     * @param operation The operation to perform.
     * @return The optional row found.
     */
    public static <T> Optional<T> doQueryOptional(HibernateSessionQueryOptional<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            return operation.fetch(session);
        }
    }

    /**
     * @param <T>       The type of result.
     * @param operation The operation to perform.
     * @return The optional row found which is either the first element or an empty option if no element.
     */
    public static <T> Optional<T> doQueryOptionalFromListOperation(HibernateSessionQueryFetch<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            List<T> items = operation.fetch(session);
            if (items.size() == 0) {
                return Optional.empty();
            } else {
                return Optional.of(items.get(0));
            }
        }
    }

    /**
     * @param <T>       The type of result.
     * @param operation The operation to perform.
     * @return The list of rows found.
     */
    public static <T> T doQuerySingle(HibernateSessionQuerySingleResult<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            return operation.fetch(session);
        }
    }

    /**
     * Bind the query parmeters in the same order.
     *
     * @param query      The Hibernate query.
     * @param parameters The parameters to bind.
     * @param <T>        The return type of the query.
     */
    public static <T> void bindParameters(Query<T> query, Object... parameters) {
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
    }

    /**
     * @param queryText  The query text.
     * @param parameters The query parameters.
     * @return True if the query returns some rows.
     */
    public static boolean doesReturnRows(String queryText, Object... parameters) {
        return doQueryOptional(s -> {
            Query<Integer> query = s.createQuery("select 1 from USERS where exists ( " + queryText + " )",
                                                 Integer.class);
            bindParameters(query, parameters);
            return query.uniqueResultOptional();
        }).isPresent();
    }

    // ================ Shortcut to save / delete an object

    /**
     * Saves the given object in a transaction.
     */
    public static void saveit(Object object) {
        doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.save(object);
            t.commit();
        });
    }

    /**
     * Updates the given object in a transaction.
     */
    public static void update(Object object) {
        doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.update(object);
            t.commit();
        });
    }

    // ================ End of interface
    // ================ Class own utilities

    /**
     * @return A new session
     */
    private static Session getASession() {
        return EMF.unwrap(SessionFactory.class)
                  .withOptions()
                  .jdbcTimeZone(TimeZone.getTimeZone("Europe/Paris"))
                  .openSession();
    }

    private static String escapeMySQL(String nameToMatch) {
        nameToMatch = nameToMatch.replaceAll("!", "!!");
        nameToMatch = nameToMatch.replaceAll("%", "!%");
        nameToMatch = nameToMatch.replaceAll("_", "!_");
        nameToMatch = nameToMatch.replaceAll("\\[", "![");
        return nameToMatch;
    }

    /**
     * @param parameter The initial parameter
     * @return Appends % to the prefix and the suffix and sanitize the data.
     */
    public static String sanitizeSQLLike(String parameter) {
        return MessageFormat.format("%{0}%", escapeMySQL(parameter).toLowerCase());
    }

}
