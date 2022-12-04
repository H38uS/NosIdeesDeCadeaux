package com.mosioj.ideescadeaux.core.model.database;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public class PreparedStatementIdKdo implements Closeable {

    private static final Logger logger = LogManager.getLogger(PreparedStatementIdKdo.class);
    private final PreparedStatement ps;

    /**
     * Does not throw but log an exception if any.
     *
     * @param ds    The data source.
     * @param query The query
     */
    public PreparedStatementIdKdo(DataSourceIdKDo ds, String query) {
        PreparedStatement temp = null;
        try {
            temp = ds.getAConnection().prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn(MessageFormat.format("Error while opening the statement: {0}", e.getMessage()));
        }
        ps = temp;
    }

    @Override
    public void close() {

        if (ps == null) {
            return;
        }

        try {
            Connection con = ps.getConnection();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Error while closing the statement: " + e.getMessage());
        }
    }

    /**
     * Binds the parameters according to their types. Supports: - String - Integer - Null
     *
     * @param parameters The query parameters.
     */
    public void bindParameters(Object... parameters) throws SQLException {

        logger.trace("Binding parameters...");
        for (int i = 0; i < parameters.length; i++) {

            Object parameter = parameters[i];
            logger.trace("Binding parameter " + i + " to " + parameter);
            if (parameter == null) {
                ps.setString(i + 1, null);
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

            if (parameter instanceof User) {
                ps.setInt(i + 1, ((User) parameter).getId());
                continue;
            }

            if (parameter instanceof Idee) {
                ps.setInt(i + 1, ((Idee) parameter).getId());
                continue;
            }

            // Default case - String
            ps.setString(i + 1, parameter.toString());
        }
    }

    public boolean execute() throws SQLException {
        return ps.execute();
    }

    public ResultSet getResultSet() throws SQLException {
        return ps.getResultSet();
    }

}
