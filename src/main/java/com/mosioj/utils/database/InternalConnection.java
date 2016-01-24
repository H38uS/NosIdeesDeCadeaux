package com.mosioj.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class InternalConnection {

	/**
	 * The internal datasource.
	 */
	private static DataSource ds;

	/**
	 * 
	 * @param query The sql query.
	 * @param parameters Optional bindable parameters.
	 * @return The result of the first row on the first column.
	 * @throws SQLException
	 */
	public static int selectInt(String query, Object... parameters) throws SQLException {

		Connection conn = ds.getConnection();
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
	public static int executeUpdate(String query, Object... parameters) throws SQLException {

		Connection conn = ds.getConnection();
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

	private static void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
		for (int i = 0; i < parameters.length; i++) {
			statement.setString(i + 1, parameters[i].toString());
		}
	}

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/web-db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
