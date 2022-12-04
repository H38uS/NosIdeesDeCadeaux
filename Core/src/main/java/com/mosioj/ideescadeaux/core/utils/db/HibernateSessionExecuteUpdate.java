package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionExecuteUpdate<T> {

    /**
     * Do some stuff using a session and returns a result.
     * The session is closed after the operation (could end in error).
     *
     * @param session The session opened for this work.
     */
    T executeUpdate(Session session);
}
