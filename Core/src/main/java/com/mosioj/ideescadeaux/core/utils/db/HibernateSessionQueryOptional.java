package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;

import java.util.Optional;

@FunctionalInterface
public interface HibernateSessionQueryOptional<T> {

    /**
     * Do some stuff using a session before returning the result.
     * The session is closed after the operation (could end in error).
     *
     * @param session The session opened for this work.
     * @return The optional row for this query.
     */
    Optional<T> fetch(Session session);
}
