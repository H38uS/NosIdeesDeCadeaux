package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.database.PreparedStatementIdKdo;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserParametersColumns;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.UserParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UserParametersRepository extends AbstractRepository {

    public static final String TABLE_NAME = "USER_PARAMETERS";
    private static final Logger logger = LogManager.getLogger(UserParametersRepository.class);

    private UserParametersRepository() {
        // Forbidden
    }

    public static void deleteAllUserParameters(int userId) {
        getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, UserParametersColumns.USER_ID), userId);
    }

    /**
     * Updates or insert a new parameter for this user.
     *
     * @param user       The user.
     * @param paramName  The parameter name.
     * @param paramValue The new value to insert/update.
     */
    public static void insertUpdateParameter(User user, String paramName, String paramValue) {
        int nb = getDb().executeUpdate(MessageFormat.format("update {0} set {1} = ? where {2} = ? and {3} = ?",
                                                            TABLE_NAME,
                                                            UserParametersColumns.PARAMETER_VALUE,
                                                            UserParametersColumns.USER_ID,
                                                            UserParametersColumns.PARAMETER_NAME),
                                       paramValue,
                                       user.id,
                                       paramName);
        if (nb == 0) {
            getDb().executeUpdate(MessageFormat.format("insert into {0} ({1}, {2}, {3}) values (?, ?, ?)",
                                                       TABLE_NAME,
                                                       UserParametersColumns.PARAMETER_VALUE,
                                                       UserParametersColumns.USER_ID,
                                                       UserParametersColumns.PARAMETER_NAME),
                                  paramValue,
                                  user.id,
                                  paramName);
        }
    }

    public static String getParameter(int userId, String paramName) throws SQLException {
        String query = MessageFormat.format("select {0} from {1} where {2} = ? and {3} = ?",
                                            UserParametersColumns.PARAMETER_VALUE,
                                            TABLE_NAME,
                                            UserParametersColumns.PARAMETER_NAME,
                                            UserParametersColumns.USER_ID);
        logger.trace(query);
        return getDb().selectString(query,
                                    paramName,
                                    userId);
    }

    /**
     * @param userId The user id.
     * @return The notification parameters for this user.
     */
    public static List<UserParameter> getUserNotificationParameters(int userId) throws SQLException {

        List<UserParameter> params = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append(MessageFormat.format(
                "select coalesce(t.{0}, -1) as {0},? as {1},n.{2},coalesce(t.{3}, n.{3}) as {3} ",
                UserParametersColumns.ID,
                UserParametersColumns.USER_ID,
                UserParametersColumns.PARAMETER_NAME,
                UserParametersColumns.PARAMETER_VALUE));

        query.append("from ( ");
        for (NotificationType type : NotificationType.values()) {
            query.append(MessageFormat.format("select ''{0}'' as {1}, ''{2}'' as {3} ",
                                              type,
                                              UserParametersColumns.PARAMETER_NAME,
                                              NotificationActivation.EMAIL_SITE,
                                              UserParametersColumns.PARAMETER_VALUE));
            query.append(" from dual union all ");
        }
        query.delete(query.length() - "union all ".length(), query.length());
        query.append("          ) n ");

        query.append(MessageFormat.format("  left join {0} t ", TABLE_NAME));
        query.append(MessageFormat.format("    on n.{0} = t.{1}", UserParametersColumns.PARAMETER_NAME, UserParametersColumns.PARAMETER_NAME));
        query.append(MessageFormat.format("   and t.{0} = ? ", UserParametersColumns.USER_ID));
        query.append(MessageFormat.format(" where t.{0} = ? or t.{0} is null ", UserParametersColumns.USER_ID));
        query.append(MessageFormat.format(" order by n.{0}", UserParametersColumns.PARAMETER_NAME));

        logger.trace(query);
        try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString())) {
            ps.bindParameters(userId, userId, userId);

            if (ps.execute()) {
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    params.add(new UserParameter(rs.getInt(UserParametersColumns.ID.name()),
                                                 rs.getInt(UserParametersColumns.USER_ID.name()),
                                                 rs.getString(UserParametersColumns.PARAMETER_NAME.name()),
                                                 rs.getString(UserParametersColumns.PARAMETER_VALUE.name()),
                                                 NotificationType.valueOf(rs.getString(UserParametersColumns.PARAMETER_NAME.name()))
                                                                 .getDescription()));
                }
            }
        }

        return params;
    }
}
