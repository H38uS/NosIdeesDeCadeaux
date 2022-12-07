package com.mosioj.ideescadeaux.core.model.database;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Provides some method to access the database.
 *
 * @author Jordan Mosio
 */
public class DataSourceIdKDo {

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public int selectCountStar(String query, Object... parameters) {
        return selectInt(query, parameters).orElse(0);
    }


    /**
     * Bind the query parmeters in the same order.
     *
     * @param query      The Hibernate query.
     * @param parameters The parameters to bind.
     * @param <T>        The return type of the query.
     */
    public static <T> void bindParameters(Query<T> query, Object... parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Object p = parameters[i];
            if (p == null) {
                query.setParameter(i + 1, null);
            } else if (p instanceof Double || p instanceof Integer) {
                query.setParameter(i + 1, p);
            } else if (p instanceof User) {
                query.setParameter(i + 1, ((User) p).getId());
            } else if (p instanceof Idee) {
                query.setParameter(i + 1, ((Idee) p).getId());
            } else {
                query.setParameter(i + 1, p.toString());
            }
        }
    }

    /**
     * @param query      The sql query.
     * @param parameters Optional bindable parameters.
     * @return The result of the first row on the first column.
     */
    public Optional<Integer> selectInt(String query, Object... parameters) {
        return HibernateUtil.doQuerySingle(s -> {
            // FIXME : faudra faire autrement
            final NativeQuery<?> sqlQuery = s.createSQLQuery(query);
            bindParameters(sqlQuery, parameters);
            return sqlQuery.uniqueResultOptional().map(res -> {
                if (res instanceof BigInteger)
                    return ((BigInteger) res).intValue();
                return (Integer) res;
            });
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
            final NativeQuery<?> sqlQuery = s.createSQLQuery(query);
            bindParameters(sqlQuery, parameters);
            return sqlQuery.executeUpdate();
        });
    }

    /**
     * Execute an insert statement and return the generated key.
     *
     * @param query      The SQL query.
     * @param parameters Optional bindable parameters.
     */
    public void executeInsert(String query, Object... parameters) {
        HibernateUtil.doSomeExecutionWork(s -> {
            final NativeQuery<?> sqlQuery = s.createSQLQuery(query);
            bindParameters(sqlQuery, parameters);
            sqlQuery.executeUpdate();
            BigInteger result = (BigInteger) s.createSQLQuery("SELECT LAST_INSERT_ID()").uniqueResult();
            return result.intValue();
        });
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
