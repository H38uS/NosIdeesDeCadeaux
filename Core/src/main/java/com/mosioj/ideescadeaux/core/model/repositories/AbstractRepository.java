package com.mosioj.ideescadeaux.core.model.repositories;

import java.text.MessageFormat;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;

public abstract class AbstractRepository {

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

    protected static String escapeMySQL(String nameToMatch) {
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
    protected static String sanitizeSQLLike(String parameter) {
        return MessageFormat.format("%{0}%", escapeMySQL(parameter).toLowerCase());
    }

}
