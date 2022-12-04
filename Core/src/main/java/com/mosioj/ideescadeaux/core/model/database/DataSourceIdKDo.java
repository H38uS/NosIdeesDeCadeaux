package com.mosioj.ideescadeaux.core.model.database;

import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Connection;
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
        return HibernateUtil.doQuerySingle(s -> {
            // FIXME : faudra faire autrement
            final Query<BigInteger> sqlQuery = s.createSQLQuery(query);
            for (int i = 0; i < parameters.length; i++) {
                sqlQuery.setParameter(i + 1, parameters[i]);
            }
            return sqlQuery.uniqueResultOptional().map(BigInteger::intValue);
        });
    }

    /**
     * Execute a DML statement : insert / update / delete.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     * @return The number of rows inserted / updated / deleted.
     */
    public int executeUpdate(String query, Object... parameters) {
        return HibernateUtil.doSomeExecutionWork(s -> {
            final Query<?> sqlQuery = s.createSQLQuery(query);
            for (int i = 0; i < parameters.length; i++) {
                sqlQuery.setParameter(i + 1, parameters[i]);
            }
            return sqlQuery.executeUpdate();
        });
    }

    /**
     * Execute an insert statement and return the generated key.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     * @return The number of rows inserted / updated / deleted.
     */
    public int executeInsert(String query, Object... parameters) {
        return HibernateUtil.doSomeExecutionWork(s -> {
            final Query<?> sqlQuery = s.createSQLQuery(query);
            for (int i = 0; i < parameters.length; i++) {
                sqlQuery.setParameter(i + 1, parameters[i]);
            }
            sqlQuery.executeUpdate();
            BigInteger result = (BigInteger) s.createSQLQuery("SELECT LAST_INSERT_ID()").uniqueResult();
            return result.intValue();
        });
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
        return selectInt(query, parameters).isPresent();
    }

}
