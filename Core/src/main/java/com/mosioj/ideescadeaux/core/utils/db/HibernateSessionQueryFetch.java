package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;

import java.util.List;

@FunctionalInterface
public interface HibernateSessionQueryFetch<T> {

    /**
     * Do some stuff using a session before returning the result.
     * The session is closed after the operation (could end in error).
     *
     * @param session The session opened for this work.
     * @return The rows for this query.
     */
    List<T> fetch(Session session);
}
