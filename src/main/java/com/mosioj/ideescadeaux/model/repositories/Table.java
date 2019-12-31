package com.mosioj.ideescadeaux.model.repositories;

import java.text.MessageFormat;

import com.mosioj.ideescadeaux.utils.database.DataSourceIdKDo;

public abstract class Table {

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

    protected String escapeMySQL(String nameToMatch) {
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
    protected String sanitizeSQLLike(String parameter) {
        return MessageFormat.format("%{0}%", escapeMySQL(parameter).toLowerCase());
    }

}
