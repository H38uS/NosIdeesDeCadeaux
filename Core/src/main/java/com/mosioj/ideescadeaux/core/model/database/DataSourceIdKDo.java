package com.mosioj.ideescadeaux.core.model.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Provides some method to access the database.
 *
 * @author Jordan Mosio
 */
public class DataSourceIdKDo {

    private final Logger logger = LogManager.getLogger(DataSourceIdKDo.class);

    /**
     * The internal datasource.
     */
    private static DataSource ds;

    /**
     * @return A new connection. Warning : it must be closed.
     */
    protected Connection getAConnection() {
        try {
            return getDatasource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Fail to get a connection... => " + e.getMessage());
            throw new RuntimeException("Impossible to get a connection...");
        }
    }

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public int selectCountStar(String query, Object... parameters) {
        return selectInt(query, parameters).orElse(0);
    }

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public Optional<Integer> selectInt(String query, Object... parameters) {
        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {
            statement.bindParameters(parameters);
            if (statement.execute()) {
                ResultSet res = statement.getResultSet();
                if (res.next()) {
                    int value = res.getInt(1);
                    return res.wasNull() ? Optional.empty() : Optional.of(value);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return Optional.empty();
    }

    /**
     * Execute a DML statement : insert / update / delete.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     * @return The number of rows inserted / updated / deleted.
     */
    public int executeUpdate(String query, Object... parameters) throws SQLException {
        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {
            statement.bindParameters(parameters);
            return statement.executeUpdate();
        }
    }

    /**
     * Execute a DML statement : insert / update / delete.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     * @return The generated key value.
     */
    public int executeUpdateGeneratedKey(String query, Object... parameters) throws SQLException {
        try (PreparedStatementIdKdoInserter statement = new PreparedStatementIdKdoInserter(this, query)) {
            statement.bindParameters(parameters);
            return statement.executeUpdate();
        }
    }

    /**
     * !!!! Only for test !!!!
     *
     * @param newDS The new data source.
     */
    public static void setDataSource(DataSource newDS) {
        ds = newDS;
    }

    /**
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
     * @param query      The initial query.
     * @param parameters The query parameters.
     * @return True if and only if the query returns at least one row and there are no errors.
     */
    public boolean doesReturnRows(String query, Object... parameters) {
        query = "select 1 from dual where exists ( " + query + " )";
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(this, query)) {
            ps.bindParameters(parameters);
            ps.execute();
            ResultSet res = ps.getResultSet();
            return res.next();
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public Optional<String> selectString(String query, Object... parameters) throws SQLException {
        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {
            statement.bindParameters(parameters);
            if (statement.execute()) {
                ResultSet res = statement.getResultSet();
                if (res.next()) {
                    return Optional.ofNullable(res.getString(1));
                }
            }
        }
        return Optional.empty();
    }

}
