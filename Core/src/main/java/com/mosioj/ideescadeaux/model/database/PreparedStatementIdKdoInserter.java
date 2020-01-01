package com.mosioj.ideescadeaux.model.database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;

public class PreparedStatementIdKdoInserter implements Closeable {

    private static final Logger logger = LogManager.getLogger(PreparedStatementIdKdoInserter.class);
    private final PreparedStatement ps;

    public PreparedStatementIdKdoInserter(DataSourceIdKDo ds, String query) {
        PreparedStatement temp = null;
        try {
            temp = ds.getAConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("Error while executing update: {0}.", e.getMessage()));
        }
        ps = temp;
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
            logger.error("Error while closing the statement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Binds the parameters according to their types. Supports: - String - Integer - Null
     *
     * @param parameters The query parameters.
     */
    public void bindParameters(Object... parameters) {

        logger.trace("Binding parameters...");

        try {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                logger.trace("Binding parameter " + i + " to " + parameter);
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
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("Error while binding parameters: {0}.", e.getMessage()));
        }
    }

    /**
     * @return The generated key.
     */
    public int executeUpdate() {
        try {
            ps.executeUpdate();
            ResultSet res = ps.getGeneratedKeys();
            res.next();
            return res.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("Error while executing update: {0}.", e.getMessage()));
            return 0;
        }
    }

}
