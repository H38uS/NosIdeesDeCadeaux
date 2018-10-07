package com.mosioj.model.table;

import static com.mosioj.model.table.columns.UserParametersColumns.ID;
import static com.mosioj.model.table.columns.UserParametersColumns.PARAMETER_NAME;
import static com.mosioj.model.table.columns.UserParametersColumns.PARAMETER_VALUE;
import static com.mosioj.model.table.columns.UserParametersColumns.USER_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.UserParameter;
import com.mosioj.notifications.NotificationActivation;
import com.mosioj.notifications.NotificationType;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class UserParameters extends Table {

	public static final String TABLE_NAME = "USER_PARAMETERS";
	private static final Logger logger = LogManager.getLogger(UserParameters.class);

	public void deleteAllUserParameters(int userId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, USER_ID), userId);
	}

	public void insertUpdateParameter(int userId, String paramName, String paramValue) throws SQLException {
		int nb = getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ? where {2} = ? and {3} = ?",
																TABLE_NAME,
																PARAMETER_VALUE,
																USER_ID,
																PARAMETER_NAME),
										paramValue,
										userId,
										paramName);
		if (nb == 0) {
			getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1}, {2}, {3}) values (?, ?, ?)",
															TABLE_NAME,
															PARAMETER_VALUE,
															USER_ID,
															PARAMETER_NAME),
									paramValue,
									userId,
									paramName);
		}
	}

	public String getParameter(int userId, String paramName) throws SQLException {
		return getDb().selectString(MessageFormat.format(	"select {0} from {1} where {2} = ? and {3} = ?",
															PARAMETER_VALUE,
															TABLE_NAME,
															PARAMETER_NAME,
															USER_ID),
									paramName,
									userId);
	}

	/**
	 * 
	 * @param userId
	 * @return The notification parameters for this user.
	 * @throws SQLException
	 */
	public List<UserParameter> getUserNotificationParameters(int userId) throws SQLException {

		List<UserParameter> params = new ArrayList<UserParameter>();

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format(	"select coalesce(t.{0}, -1) as {0},? as {1},n.{2},coalesce(t.{3}, n.{3}) as {3} ",
											ID,
											USER_ID,
											PARAMETER_NAME,
											PARAMETER_VALUE));

		query.append("from ( ");
		for (NotificationType type : NotificationType.values()) {
			query.append(MessageFormat.format(	"select ''{0}'' as {1}, ''{2}'' as {3} ",
												type,
												PARAMETER_NAME,
												NotificationActivation.EMAIL_SITE,
												PARAMETER_VALUE));
			query.append(" from dual union all ");
		}
		query.delete(query.length() - "union all ".length(), query.length());
		query.append("          ) n ");

		query.append(MessageFormat.format("  left join {0} t ", TABLE_NAME));
		query.append(MessageFormat.format("    on n.{0} = t.{1}", PARAMETER_NAME, PARAMETER_NAME));
		query.append(MessageFormat.format("   and t.{0} = ? ", USER_ID));
		query.append(MessageFormat.format(" where t.{0} = ? or t.{0} is null ", USER_ID));
		query.append(MessageFormat.format(" order by n.{0}", PARAMETER_NAME));

		logger.trace(query);
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(userId, userId, userId);

			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					params.add(new UserParameter(	rs.getInt(ID.name()),
													rs.getInt(USER_ID.name()),
													rs.getString(PARAMETER_NAME.name()),
													rs.getString(PARAMETER_VALUE.name()),
													NotificationType.valueOf(rs.getString(PARAMETER_NAME.name())).getDescription()));
				}
			}
		} finally {
			ps.close();
		}

		return params;
	}
}
