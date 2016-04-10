package com.mosioj.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Provides some method to access the database.
 * 
 * @author Jordan Mosio
 *
 */
public class ConnectionIdKDo {

	/**
	 * The internal datasource.
	 */
	private static DataSource ds;
	
	/**
	 * Class logger.
	 */
	private static final Logger logger = Logger.getLogger(ConnectionIdKDo.class);

	/**
	 * 
	 * @return A new connection. Warning : it must be closed.
	 * @throws SQLException
	 */
	public Connection getAConnection() throws SQLException {
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

		Connection conn = getAConnection();
		try {

			PreparedStatement statement = conn.prepareStatement(query);
			bindParameters(statement, parameters);

			if (!statement.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = statement.getResultSet();
			if (!res.first()) {
				throw new SQLException("No rows retrieved."); // TODO : faire une exception différente !!
			}

			return res.getInt(1);

		} finally {
			conn.close();
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

		Connection conn = getAConnection();
		int retour = 0;

		try {
			PreparedStatement statement = conn.prepareStatement(query);
			bindParameters(statement, parameters);
			retour = statement.executeUpdate();
		} finally {
			conn.close();
		}

		return retour;
	}

	/**
	 * Binds the parameters according to their types. Supports: - String - Integer - Null
	 * 
	 * @param statement
	 * @param parameters
	 * @throws SQLException
	 */
	public void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {

		logger.trace("Binding parameters...");
		for (int i = 0; i < parameters.length; i++) {

			Object parameter = parameters[i];
			logger.trace("Binding parameter " + i + " to " + parameter);
			if (parameter == null) {
				statement.setString(i + 1, null);
				continue;
			}

			if (parameter instanceof Integer) {
				statement.setInt(i + 1, (Integer) parameter);
				continue;
			}

			// Default case - String
			statement.setString(i + 1, parameter.toString());
		}

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

		Connection con = getAConnection();

		try {
			query = "select 1 from dual where exists ( " + query + " )";
			PreparedStatement ps = con.prepareStatement(query);
			bindParameters(ps, parameters);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			return res.first();

		} finally {
			con.close();
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

		Connection conn = getAConnection();
		try {

			PreparedStatement statement = conn.prepareStatement(query);
			bindParameters(statement, parameters);

			if (!statement.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = statement.getResultSet();
			if (!res.first()) {
				throw new SQLException("No rows retrieved.");
			}

			return res.getString(1);

		} finally {
			conn.close();
		}
	}

}
