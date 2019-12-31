package com.mosioj.ideescadeaux.utils.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    } // FIXME : 0 revoir les exceptions + séparer en deux modules

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public int selectCountStar(String query, Object... parameters) throws SQLException {
        try {
            return selectInt(query, parameters);
        } catch (NoRowsException e) {
            return 0;
        }
    }

	/**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public int selectInt(String query, Object... parameters) throws SQLException, NoRowsException {

        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {

            statement.bindParameters(parameters);

            if (!statement.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = statement.getResultSet();
            if (!res.first()) {
                throw new NoRowsException();
            }

            return res.getInt(1);

        }
    }

    /**
     * Execute a DML statement : insert / update / delete.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     * @return The number of rows inserted / updated / deleted.
     */
    public int executeUpdate(String query, Object... parameters) {

        int retour = 0;

        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {
            statement.bindParameters(parameters);
            retour = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(MessageFormat.format("Error while executing update: {0}.", e.getMessage()));
        }

        return retour;
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
     * @return True if and only if the query returns at least one row.
     */
    public boolean doesReturnRows(String query, Object... parameters) throws SQLException {
        query = "select 1 from dual where exists ( " + query + " )";
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(this, query)) {
            ps.bindParameters(parameters);

            if (!ps.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = ps.getResultSet();
            return res.first();

        }
    }

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public String selectString(String query, Object... parameters) throws SQLException {

        try (PreparedStatementIdKdo statement = new PreparedStatementIdKdo(this, query)) {

            statement.bindParameters(parameters);

            if (!statement.execute()) {
                throw new SQLException("No result set available.");
            }

            ResultSet res = statement.getResultSet();
            if (!res.first()) {
                throw new SQLException("No rows retrieved.");
            }

            return res.getString(1);

        }
    }

}
