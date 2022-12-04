package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;

public abstract class AbstractRepository {

    // FIXME : à supprimer quand utilise que Hibernate?

    /**
     * The database specific connection.
     */
    private static final DataSourceIdKDo db = new DataSourceIdKDo();

    /**
     * @return The DB connection manager to use.
     */
    protected static DataSourceIdKDo getDb() {
        return db;
    }

}
