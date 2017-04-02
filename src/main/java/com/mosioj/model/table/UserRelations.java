package com.mosioj.model.table;

import static com.mosioj.model.table.columns.UserRelationsColumns.FIRST_USER;
import static com.mosioj.model.table.columns.UserRelationsColumns.RELATION_DATE;
import static com.mosioj.model.table.columns.UserRelationsColumns.SECOND_USER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Relation;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.UsersColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class UserRelations extends Table {

	public static final String TABLE_NAME = "USER_RELATIONS";
	private static final Logger logger = LogManager.getLogger(UserRelations.class);

	/**
	 * 
	 * @param user The user.
	 * @return The list of relations this use has.
	 * @throws SQLException
	 */
	public List<Relation> getRelations(int user) throws SQLException {

		List<Relation> relations = new ArrayList<Relation>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select {0}, {1}, u1.{6} as first_name, u1.{7} as first_email, u2.{6} as second_name, u2.{7} as second_email ");
		query.append("from {2} urr ");
		query.append("left join {5} u1 on u1.id = urr.{0} ");
		query.append("left join {5} u2 on u2.id = urr.{1} ");
		query.append("where {3} = ? ");

		try {
			ps = new PreparedStatementIdKdo(getDb(),
											MessageFormat.format(	query.toString(),
																	FIRST_USER.name(),
																	SECOND_USER.name(),
																	TABLE_NAME,
																	FIRST_USER,
																	SECOND_USER,
																	Users.TABLE_NAME,
																	UsersColumns.NAME,
																	UsersColumns.EMAIL));
			ps.bindParameters(user);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					relations.add(new Relation(	new User(	res.getInt(FIRST_USER.name()),
															res.getString("first_name"),
															res.getString("first_email")),
												new User(	res.getInt(SECOND_USER.name()),
															res.getString("second_name"),
															res.getString("second_email"))));
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return relations;
	}

	/**
	 * 
	 * @param userId
	 * @param inNbDaysMax
	 * @return The list of users with birthday coming (less than 30 days).
	 * @throws SQLException
	 */
	public List<User> getCloseBirthday(int userId, int inNbDaysMax) throws SQLException {

		List<User> users = new ArrayList<User>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select b.{0}, b.{1}, b.{2}, b.{3}, b.days_before_next_year_birthday, b.days_before_birthday ");
		query.append("from ( ");

		query.append("select a.{0}, a.{1}, a.{2}, a.{3}, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()) +1, ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_next_year_birthday, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()), ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_birthday ");
		query.append("from ( ");
		query.append("select u.{0}, u.{1}, u.{2}, u.{3} ");
		query.append("from {4} urr ");
		query.append("left join {5} u on u.{0} = urr.{7} ");
		query.append("where {6} = ? ");
		query.append(") a ");

		query.append(") b ");
		query.append("where (b.days_before_birthday >= 0 and b.days_before_birthday < ?) or b.days_before_next_year_birthday < ? ");
		query.append("order by b.days_before_next_year_birthday ");

		try {
			String realQuery = MessageFormat.format(query.toString(),
													UsersColumns.ID,
													UsersColumns.NAME,
													UsersColumns.EMAIL,
													UsersColumns.BIRTHDAY,
													TABLE_NAME,
													Users.TABLE_NAME,
													FIRST_USER,
													SECOND_USER);
			logger.trace(realQuery);
			ps = new PreparedStatementIdKdo(getDb(), realQuery);
			ps.bindParameters(userId, inNbDaysMax, inNbDaysMax);

			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					users.add(new User(	res.getInt(UsersColumns.ID.name()),
										res.getString(UsersColumns.NAME.name()),
										res.getString(UsersColumns.EMAIL.name()),
										res.getDate(UsersColumns.BIRTHDAY.name()),
										res.getInt("days_before_birthday") < 0 ? res.getInt("days_before_next_year_birthday")
												: res.getInt("days_before_birthday")));
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return users;
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @return True if and only if the two guys are friends.
	 * @throws SQLException
	 */
	public boolean associationExists(int first, int second) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME,
																FIRST_USER,
																SECOND_USER),
										first,
										second);
	}

	/**
	 * Creates a new friendship.
	 * 
	 * @param userThatSendTheRequest
	 * @param userThatReceiveTheRequest
	 * @throws SQLException
	 */
	public void addAssociation(int userThatSendTheRequest, int userThatReceiveTheRequest) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?,?,now())",
														TABLE_NAME,
														FIRST_USER,
														SECOND_USER,
														RELATION_DATE),
								userThatSendTheRequest,
								userThatReceiveTheRequest);
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?,?,now())",
														TABLE_NAME,
														FIRST_USER,
														SECOND_USER,
														RELATION_DATE),
								userThatReceiveTheRequest,
								userThatSendTheRequest);
	}

	public List<User> getAllUsersInRelation(int userId) throws SQLException {

		List<User> users = new ArrayList<User>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select u.{0}, u.{1}, u.{2} ");
		query.append("from {3} u, {4} r ");
		query.append("where u.{0} = r.{6} and r.{5} = ? ");
		query.append("order by {1}, {2}, {0}");

		try {
			ps = new PreparedStatementIdKdo(getDb(),
											MessageFormat.format(	query.toString(),
																	UsersColumns.ID.name(),
																	UsersColumns.NAME.name(),
																	UsersColumns.EMAIL.name(),
																	Users.TABLE_NAME,
																	TABLE_NAME,
																	FIRST_USER,
																	SECOND_USER));
			ps.bindParameters(userId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					users.add(new User(	res.getInt(UsersColumns.ID.name()),
										res.getString(UsersColumns.NAME.name()),
										res.getString(UsersColumns.EMAIL.name())));
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}

		return users;
	}
}
