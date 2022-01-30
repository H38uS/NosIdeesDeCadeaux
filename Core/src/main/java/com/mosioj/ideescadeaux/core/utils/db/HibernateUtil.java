package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public static <T> Set<T> doQueryFetchAsSet(HibernateSessionQueryFetchAsSet<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            return operation.fetch(session);
        }
    }

    /**
     * @param <T>       The type of result.
     * @param operation The operation to perform.
     * @return The list of rows found.
     */
    public static <T> Optional<T> doQueryOptional(HibernateSessionQueryOptional<T> operation) {
        try (Session session = HibernateUtil.getASession()) {
            return operation.fetch(session);
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
}
