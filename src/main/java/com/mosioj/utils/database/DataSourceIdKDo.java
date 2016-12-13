package com.mosioj.utils.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Provides some method to access the database.
 * 
 * @author Jordan Mosio
 *
 */
public class DataSourceIdKDo {

	/**
	 * The internal datasource.
	 */
	private static DataSource ds;
	
	/**
	 * 
	 * @return A new connection. Warning : it must be closed.
	 * @throws SQLException
	 */
	protected Connection getAConnection() throws SQLException {
		return getDatasource().getConnection();
	}
	
	/**
	 * 
	 * @param query The sql query.
	 * @param parameters Optional bindable parameters.
	 * @return The result of the first row on the first column.
	 * @throws SQLException
	 */
	public int selectInt(String query, Object... parameters) throws SQLException {

		PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query);
		try {

			statement.bindParameters(parameters);

			if (!statement.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = statement.getResultSet();
			if (!res.first()) {
				throw new SQLException("No rows retrieved."); // TODO : faire une exception différente !!
			}

			return res.getInt(1);

		} finally {
			statement.close();
		}
	}

	/**
	 * Execute a DML statement : insert / update / delete.
	 * 
	 * @param query The SQL query.
	 * @param parameters Optional bindable parameters.
	 * @return The number of rows inserted / updated / deleted.
	 * @throws SQLException
	 */
	public int executeUpdate(String query, Object... parameters) throws SQLException {

		int retour = 0;

		PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query);
		try {
			statement.bindParameters(parameters);
			retour = statement.executeUpdate();
		} finally {
			statement.close();
		}

		return retour;
	}

	/**
	 * 
	 * @return The data source to use.
	 */
	private static DataSource getDatasource() {

		if (ds == null) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				ds = (DataSource) envCtx.lookup("jdbc/web-db");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ds;
	}

	/**
	 * 
	 * @param query
	 * @param parameters
	 * @return True if and only if the query returns at least one row.
	 * @throws SQLException
	 */
	public boolean doesReturnRows(String query, Object... parameters) throws SQLException {

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(this, query);
		try {
			query = "select 1 from dual where exists ( " + query + " )";
			ps.bindParameters(parameters);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			return res.first();

		} finally {
			ps.close();
		}
	}

	/**
	 * 
	 * @param query The sql query.
	 * @param parameters Optional bindable parameters.
	 * @return The result of the first row on the first column.
	 * @throws SQLException
	 */
	public String selectString(String query, Object... parameters) throws SQLException {

		PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query);
		try {

			statement.bindParameters(parameters);

			if (!statement.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = statement.getResultSet();
			if (!res.first()) {
				throw new SQLException("No rows retrieved.");
			}

			return res.getString(1);

		} finally {
			statement.close();
		}
	}

}
