package com.mosioj.model.table;

import com.mosioj.utils.database.ConnectionIdKDo;

public abstract class Table {

	/**
	 * The database specific connection.
	 */
	private final ConnectionIdKDo db;
	
	/**
	 * Class constructor.
	 */
	public Table() {
		db = new ConnectionIdKDo();
	}

	/**
	 * 
	 * @return The DB connection manager to use.
	 */
	protected ConnectionIdKDo getDb() {
		return db;
	}
	
}
