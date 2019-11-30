package com.mosioj.ideescadeaux.utils.database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.User;

public class PreparedStatementIdKdoInserter implements Closeable {

	private static final Logger LOGGER = LogManager.getLogger(PreparedStatementIdKdoInserter.class);
	private final PreparedStatement ps;

	public PreparedStatementIdKdoInserter(DataSourceIdKDo ds, String query) throws SQLException {
		ps = ds.getAConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}

	@Override
	public void close() {
		try {
			if (!ps.isClosed()) {
				Connection con = ps.getConnection();
				ps.close();
				con.close();
			}
		} catch (SQLException e) {
			LOGGER.error("Error while closing the statement: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Binds the parameters according to their types. Supports: - String - Integer - Null
	 * 
	 * @param statement
	 * @param parameters
	 * @throws SQLException
	 */
	public void bindParameters(Object... parameters) throws SQLException {

		LOGGER.trace("Binding parameters...");
		for (int i = 0; i < parameters.length; i++) {

			Object parameter = parameters[i];
			LOGGER.trace("Binding parameter " + i + " to " + parameter);
			if (parameter == null) {
				ps.setString(i + 1, null);
				continue;
			}

			if (parameter instanceof User) {
				ps.setInt(i + 1, ((User) parameter).getId());
				continue;
			}

			if (parameter instanceof Double) {
				ps.setDouble(i + 1, (Double) parameter);
				continue;
			}

			if (parameter instanceof Integer) {
				ps.setInt(i + 1, (Integer) parameter);
				continue;
			}

			// Default case - String
			ps.setString(i + 1, parameter.toString());
		}

	}

	/**
	 * 
	 * @return The generated key.
	 * @throws SQLException
	 */
	public int executeUpdate() throws SQLException {
		ps.executeUpdate();
		ResultSet res = ps.getGeneratedKeys();
		res.next();
		return res.getInt(1);
	}

}
