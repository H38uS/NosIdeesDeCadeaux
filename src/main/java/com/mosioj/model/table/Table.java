package com.mosioj.model.table;

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
	
}
