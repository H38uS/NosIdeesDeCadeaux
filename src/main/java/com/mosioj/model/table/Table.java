package com.mosioj.model.table;

import java.text.MessageFormat;

import com.mosioj.utils.database.DataSourceIdKDo;

public abstract class Table {

	/**
	 * The database specific connection.
	 */
	private final DataSourceIdKDo db;
	
	/**
	 * Class constructor.
	 */
	public Table() {
		db = new DataSourceIdKDo();
	}

	/**
	 * 
	 * @return The DB connection manager to use.
	 */
	protected DataSourceIdKDo getDb() {
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
	 * 
	 * @param parameter The initial parameter
	 * @return Appends % to the prefix and the suffix and sanitize the data.
	 */
	protected String sanitizeSQLLike(String parameter) {
		return MessageFormat.format("%{0}%", escapeMySQL(parameter).toLowerCase());
	}
	
}
