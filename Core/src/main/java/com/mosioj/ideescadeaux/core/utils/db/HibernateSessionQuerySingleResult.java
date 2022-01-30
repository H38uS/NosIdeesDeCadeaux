package com.mosioj.ideescadeaux.core.utils.db;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionQuerySingleResult<T> {

    /**
     * @param session The session.
     * @return The single fetch done by this operation.
     */
    T fetch(Session session);
}
