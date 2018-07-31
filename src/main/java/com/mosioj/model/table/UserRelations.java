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
	 * @param nameOrEmail
	 * @param firstRow
	 * @param maxNumberOfRows
	 * @return The list of relations this use has that matches the given name/email.
	 * @throws SQLException
	 */
	public List<Relation> getRelations(int user, String nameOrEmail, int firstRow, int maxNumberOfRows) throws SQLException {

		List<Relation> relations = new ArrayList<Relation>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select {0}, {1}, u1.{4} as first_name, u1.{5} as first_email, u1.{6} as first_avatar, u2.{4} as second_name, u2.{5} as second_email, u2.{6} as second_avatar ");
		query.append("from {2} urr ");
		query.append("left join {3} u1 on u1.id = urr.{0} ");
		query.append("left join {3} u2 on u2.id = urr.{1} ");
		query.append("where {0} = ? ");
		query.append("  and (lower(u2.{4}) like ? ESCAPE ''!'' or lower(u2.{5}) like ? ESCAPE ''!'') ");
		query.append(" order by coalesce(u2.{4}, u2.{5}) ");
		query.append(" LIMIT ?, ? ");

		String formatedQuery = MessageFormat.format(query.toString(),
													FIRST_USER,
													SECOND_USER,
													TABLE_NAME,
													Users.TABLE_NAME,
													UsersColumns.NAME,
													UsersColumns.EMAIL,
													UsersColumns.AVATAR);
		logger.trace(formatedQuery);

		try {

			ps = new PreparedStatementIdKdo(getDb(), formatedQuery);
			nameOrEmail = sanitizeSQLLike(nameOrEmail);
			ps.bindParameters(user, nameOrEmail, nameOrEmail, firstRow, maxNumberOfRows);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					relations.add(new Relation(	new User(	res.getInt(FIRST_USER.name()),
															res.getString("first_name"),
															res.getString("first_email"),
															res.getString("first_avatar")),
												new User(	res.getInt(SECOND_USER.name()),
															res.getString("second_name"),
															res.getString("second_email"),
															res.getString("second_avatar"))));
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
	 * @param nameOrEmail
	 * @return The number of user in this user network.
	 * @throws SQLException
	 */
	public int getRelationsCount(int userId, String nameOrEmail) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append("select count(*) ");
		query.append("from {0} urr ");
		query.append("left join {1} u1 on u1.id = urr.{2} ");
		query.append("where {3} = ? ");
		query.append("  and (lower(u1.{4}) like ? ESCAPE ''!'' or lower(u1.{5}) like ? ESCAPE ''!'') ");

		String formatedQuery = MessageFormat.format(query.toString(),
													TABLE_NAME,
													Users.TABLE_NAME,
													SECOND_USER,
													FIRST_USER,
													UsersColumns.NAME,
													UsersColumns.EMAIL);

		return getDb().selectCountStar(formatedQuery, userId, sanitizeSQLLike(nameOrEmail), sanitizeSQLLike(nameOrEmail));
	}

	/**
	 * 
	 * @param userId
	 * @return The number of user in this user network.
	 * @throws SQLException
	 */
	public int getRelationsCount(int userId) throws SQLException {
		return getDb().selectCountStar(MessageFormat.format("select count(*) from {0} where {1} = ?", TABLE_NAME, FIRST_USER), userId);
	}

	/**
	 * 
	 * @param user The user.
	 * @return The list of relations this use has.
	 * @throws SQLException
	 */
	public List<Relation> getRelations(int user, int firstRow, int maxNumberOfRows) throws SQLException {

		List<Relation> relations = new ArrayList<Relation>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select {0}, {1}, u1.{6} as first_name, u1.{7} as first_email, u1.{8} as first_avatar, u2.{6} as second_name, u2.{7} as second_email, u2.{8} as second_avatar ");
		query.append("  from {2} urr ");
		query.append("  left join {5} u1 on u1.id = urr.{0} ");
		query.append("  left join {5} u2 on u2.id = urr.{1} ");
		query.append(" where {3} = ? ");
		query.append(" order by coalesce(u2.{6}, u2.{7}) ");
		query.append(" LIMIT ?, ? ");

		String formatedQuery = MessageFormat.format(query.toString(),
													FIRST_USER.name(),
													SECOND_USER.name(),
													TABLE_NAME,
													FIRST_USER,
													SECOND_USER,
													Users.TABLE_NAME,
													UsersColumns.NAME,
													UsersColumns.EMAIL,
													UsersColumns.AVATAR);
		logger.trace(formatedQuery);

		try {

			ps = new PreparedStatementIdKdo(getDb(), formatedQuery);
			ps.bindParameters(user, firstRow, maxNumberOfRows);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					relations.add(new Relation(	new User(	res.getInt(FIRST_USER.name()),
															res.getString("first_name"),
															res.getString("first_email"),
															res.getString("first_avatar")),
												new User(	res.getInt(SECOND_USER.name()),
															res.getString("second_name"),
															res.getString("second_email"),
															res.getString("second_avatar"))));
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
		query.append("select b.{0}, b.{1}, b.{2}, b.{3}, b.{8}, b.days_before_next_year_birthday, b.days_before_birthday ");
		query.append("from ( ");

		query.append("select a.{0}, a.{1}, a.{2}, a.{3}, a.{8}, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()) +1, ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_next_year_birthday, TIMESTAMPDIFF(DAY, CURDATE(), STR_TO_DATE( CONCAT(YEAR(CURDATE()), ''-'', MONTH(a.{3}), ''-'', DAY(a.{3}) ), ''%Y-%m-%d'' )) as days_before_birthday ");
		query.append("from ( ");
		query.append("select u.{0}, u.{1}, u.{2}, u.{3}, u.{8} ");
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
													SECOND_USER,
													UsersColumns.AVATAR);
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
										res.getString(UsersColumns.AVATAR.name()),
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
	 * @return True if and only if the two guys are friends. False for the owner.
	 * @throws SQLException
	 */
	public boolean associationExists(int first, int second) throws SQLException {
		String query = MessageFormat.format("select 1 from {0} where {1} = ? and {2} = ?", TABLE_NAME, FIRST_USER, SECOND_USER);
		return getDb().doesReturnRows(query, first, second);
	}

	/**
	 * Creates a new friendship.
	 * 
	 * @param userThatSendTheRequest
	 * @param userThatReceiveTheRequest
	 * @throws SQLException
	 */
	public void addAssociation(int userThatSendTheRequest, int userThatReceiveTheRequest) throws SQLException {
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?,?,now())",
																	TABLE_NAME,
																	FIRST_USER,
																	SECOND_USER,
																	RELATION_DATE),
											userThatSendTheRequest,
											userThatReceiveTheRequest);
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?,?,now())",
																	TABLE_NAME,
																	FIRST_USER,
																	SECOND_USER,
																	RELATION_DATE),
											userThatReceiveTheRequest,
											userThatSendTheRequest);
	}

	/**
	 * Drops a friendship.
	 * 
	 * @param firstUserId
	 * @param secondUserId
	 * @throws SQLException
	 */
	public void deleteAssociation(int firstUserId, int secondUserId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where ({1} = ? and {2} = ?) or ({1} = ? and {2} = ?)",
														TABLE_NAME,
														FIRST_USER,
														SECOND_USER),
								firstUserId,
								secondUserId,
								secondUserId,
								firstUserId);
	}

	public List<User> getAllNamesOrEmailsInRelation(int userId, String userNameOrEmail, int firstRow, int limit) throws SQLException {

		List<User> users = new ArrayList<User>();
		userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("select u.{0}, u.{1}, u.{2} ", UsersColumns.ID, UsersColumns.NAME, UsersColumns.EMAIL));
		query.append(MessageFormat.format("  from {0} u, {1} r ", Users.TABLE_NAME, TABLE_NAME));
		query.append(MessageFormat.format(" where u.{0} = r.{1} ", UsersColumns.ID, FIRST_USER));
		query.append(MessageFormat.format("   and r.{0} = ? ", SECOND_USER));
		query.append(MessageFormat.format("   and (lower(u.{0}) like ? ESCAPE ''!'' ", UsersColumns.NAME));
		query.append(MessageFormat.format("   or lower(u.{0}) like ? ESCAPE ''!'') ", UsersColumns.EMAIL));
		query.append(" order by 1 ");
		query.append(" LIMIT ?, ? ");

		try {
			ps = new PreparedStatementIdKdo(getDb(), query.toString());
			ps.bindParameters(userId, userNameOrEmail, userNameOrEmail, firstRow, limit);

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

	/**
	 * 
	 * @param suggestedBy
	 * @param suggestedTo
	 * @param userNameOrEmail
	 * @param firstRow
	 * @param limit
	 * @return All users matching the name/email that are in suggestedBy network, but not in suggestedTo network.
	 * @throws SQLException
	 */
	public List<User> getAllUsersInRelationNotInOtherNetwork(	int suggestedBy,
																int suggestedTo,
																String userNameOrEmail,
																int firstRow,
																int limit) throws SQLException {

		List<User> users = new ArrayList<User>();
		PreparedStatementIdKdo ps = null;
		logger.debug(suggestedBy + " / " + suggestedTo + " / " + userNameOrEmail);

		StringBuilder query = new StringBuilder();
		query.append("select u.{0}, u.{1}, u.{2} ");
		query.append("  from {3} u, {4} r ");
		query.append(" where u.{0} = r.{6} and r.{5} = ? ");
		query.append("   and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
		query.append("   and not exists ");
		query.append("       ( ");
		query.append("          select 1 ");
		query.append("            from {4} r2 ");
		query.append("            join {3} u2 ");
		query.append("              on u2.{0} = r2.{6} and r2.{5} = ? ");
		query.append("           where (lower(u2.{1}) like ? ESCAPE ''!'' or lower(u2.{2}) like ? ESCAPE ''!'') ");
		query.append("             and r.{6} = r2.{6} ");
		query.append("       ) ");
		query.append(" order by {1}, {2}, {0} ");
		query.append(" LIMIT ?, ? ");

		try {
			String formatedQuery = MessageFormat.format(query.toString(),
														UsersColumns.ID.name(),
														UsersColumns.NAME.name(),
														UsersColumns.EMAIL.name(),
														Users.TABLE_NAME,
														TABLE_NAME,
														FIRST_USER,
														SECOND_USER);

			ps = new PreparedStatementIdKdo(getDb(), formatedQuery);
			userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
			ps.bindParameters(	suggestedBy,
								userNameOrEmail,
								userNameOrEmail,
								suggestedTo,
								userNameOrEmail,
								userNameOrEmail,
								firstRow,
								limit);
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

	public List<User> getAllUsersInRelation(int userId, String userNameOrEmail, int firstRow, int limit) throws SQLException {

		List<User> users = new ArrayList<User>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select u.{0}, u.{1}, u.{2} ");
		query.append("from {3} u, {4} r ");
		query.append("where u.{0} = r.{6} and r.{5} = ? ");
		query.append("  and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
		query.append("order by {1}, {2}, {0} ");
		query.append(" LIMIT ?, ? ");

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
			userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
			ps.bindParameters(userId, userNameOrEmail, userNameOrEmail, firstRow, limit);
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

	/**
	 * 
	 * @param userId
	 * @param userNameOrEmail
	 * @return The number of users belonging to userId network and matching name/email
	 * @throws SQLException
	 */
	public int getAllUsersInRelationCount(int userId, String userNameOrEmail) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append("select count(*) ");
		query.append("from {3} u, {4} r ");
		query.append("where u.{0} = r.{6} and r.{5} = ? ");

		if (userNameOrEmail != null && !userNameOrEmail.isEmpty()) {
			query.append("  and (lower(u.{1}) like ? ESCAPE ''!'' or lower(u.{2}) like ? ESCAPE ''!'') ");
		}

		query.append("order by u.{1}, u.{2}, u.{0}");

		String formatQuery = MessageFormat.format(	query.toString(),
													UsersColumns.ID.name(),
													UsersColumns.NAME.name(),
													UsersColumns.EMAIL.name(),
													Users.TABLE_NAME,
													TABLE_NAME,
													FIRST_USER,
													SECOND_USER);
		logger.trace(formatQuery);

		if (userNameOrEmail != null && !userNameOrEmail.isEmpty()) {
			userNameOrEmail = sanitizeSQLLike(userNameOrEmail);
			return getDb().selectCountStar(formatQuery, userId, userNameOrEmail, userNameOrEmail);
		} else {
			return getDb().selectCountStar(formatQuery, userId);
		}
	}

	/**
	 * 
	 * @param userId
	 * @return All user friends, without him.
	 * @throws SQLException
	 */
	public List<User> getAllUsersInRelation(int userId) throws SQLException {
		return getAllUsersInRelation(userId, -1, -1);
	}

	public List<User> getAllUsersInRelation(int userId, int firstRow, int limit) throws SQLException {

		List<User> users = new ArrayList<User>();
		PreparedStatementIdKdo ps = null;

		StringBuilder query = new StringBuilder();
		query.append("select u.{0}, u.{1}, u.{2} ");
		query.append("from {3} u, {4} r ");
		query.append("where u.{0} = r.{6} and r.{5} = ? ");
		query.append("order by {1}, {2}, {0}");
		if (firstRow > -1 && limit > 0) {
			query.append(" LIMIT ?, ? ");
		}

		try {
			String q = MessageFormat.format(query.toString(),
											UsersColumns.ID.name(),
											UsersColumns.NAME.name(),
											UsersColumns.EMAIL.name(),
											Users.TABLE_NAME,
											TABLE_NAME,
											FIRST_USER,
											SECOND_USER);
			logger.trace(q);
			ps = new PreparedStatementIdKdo(getDb(), q);
			if (firstRow > -1 && limit > 0) {
				ps.bindParameters(userId, firstRow, limit);
			} else {
				ps.bindParameters(userId);
			}
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
