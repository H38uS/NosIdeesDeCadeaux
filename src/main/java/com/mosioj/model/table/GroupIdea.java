package com.mosioj.model.table;

import static com.mosioj.model.table.columns.GroupIdeaColumns.ID;
import static com.mosioj.model.table.columns.GroupIdeaColumns.NEEDED_PRICE;
import static com.mosioj.model.table.columns.GroupIdeaContentColumns.GROUP_ID;
import static com.mosioj.model.table.columns.GroupIdeaContentColumns.JOIN_DATE;
import static com.mosioj.model.table.columns.GroupIdeaContentColumns.PRICE;
import static com.mosioj.model.table.columns.GroupIdeaContentColumns.USER_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.IdeaGroup;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.IdeeColumns;
import com.mosioj.model.table.columns.UsersColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class GroupIdea extends Table {

	private static final Logger LOGGER = LogManager.getLogger(GroupIdea.class);

	public static final String TABLE_NAME = "GROUP_IDEA";
	public static final String TABLE_NAME_CONTENT = "GROUP_IDEA_CONTENT";

	/**
	 * Creates an initial group for an idea. Does not map it to the idea.
	 * 
	 * @param total
	 * @param amount
	 * @param userId First user belonging to this new group.
	 * @return
	 * @throws SQLException
	 */
	public int createAGroup(double total, double amount, int userId) throws SQLException {
		int id = getDb().executeUpdateGeneratedKey(	MessageFormat.format("insert into {0} ({1}) values (?)", TABLE_NAME, NEEDED_PRICE),
													total);
		addNewAmount(amount, userId, id);
		return id;
	}

	/**
	 * Adds a new participation.
	 * 
	 * @param amount
	 * @param userId
	 * @param groupId
	 * @return
	 * @throws SQLException
	 */
	private int addNewAmount(double amount, int userId, int groupId) throws SQLException {
		return getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3},{4}) values (?, ?, ?, now())",
																			TABLE_NAME_CONTENT,
																			GROUP_ID,
																			USER_ID,
																			PRICE,
																			JOIN_DATE),
													groupId,
													userId,
													amount);
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 * @throws SQLException
	 */
	public Optional<IdeaGroup> getGroupDetails(int groupId) throws SQLException {

		Optional<IdeaGroup> group = Optional.empty();
		StringBuilder q = new StringBuilder();
		q.append("select gi.{0}, gic.{1}, gic.{2}, u.{8}, u.{9}, u.{10} \n ");
		q.append("  from {3} gi, {4} gic \n ");
		q.append("  left join {7} u on u.id = gic.{1} \n ");
		q.append(" where gi.{5} = gic.{6} and gi.{5} = ? ");
		
		LOGGER.debug(q.toString());

		String query = MessageFormat.format(q.toString(),
											NEEDED_PRICE,
											USER_ID,
											PRICE,
											TABLE_NAME,
											TABLE_NAME_CONTENT,
											ID,
											GROUP_ID,
											Users.TABLE_NAME,
											UsersColumns.NAME,
											UsersColumns.EMAIL,
											UsersColumns.AVATAR);

		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(getDb(), query);
			ps.bindParameters(groupId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();

				if (res.next()) {
					group = Optional.of(new IdeaGroup(groupId, res.getDouble(NEEDED_PRICE.name())));
					group.get().addUser(	new User(	res.getInt(USER_ID.name()),
												res.getString(UsersColumns.NAME.name()),
												res.getString(UsersColumns.EMAIL.name()),
												res.getString(UsersColumns.AVATAR.name())),
									res.getDouble(PRICE.name()));
				}

				while (res.next()) {
					group.get().addUser(	new User(	res.getInt(USER_ID.name()),
												res.getString(UsersColumns.NAME.name()),
												res.getString(UsersColumns.EMAIL.name()),
												res.getString(UsersColumns.AVATAR.name())),
									res.getDouble(PRICE.name()));
				}
			}
		} finally {
			if (ps != null)
				ps.close();
		}

		return group;
	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @param newAmount
	 * @return True if and only if the user is a new participant.
	 * @throws SQLException
	 */
	public boolean updateAmount(Integer groupId, int userId, double newAmount) throws SQLException {
		try {
			addNewAmount(newAmount, userId, groupId);
			return true;
		} catch (SQLException e) {
			getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ? where {2} = ? and {3} = ?",
															TABLE_NAME_CONTENT,
															PRICE,
															USER_ID,
															GROUP_ID),
									newAmount,
									userId,
									groupId);
			return false;
		}
	}

	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @return true if there is at least one member left
	 * @throws SQLException
	 */
	public boolean removeUserFromGroup(int userId, Integer groupId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
														TABLE_NAME_CONTENT,
														USER_ID,
														GROUP_ID),
								userId,
								groupId);
		if (!getDb().doesReturnRows(MessageFormat.format("select 1 from {0} where {1} = ? ", TABLE_NAME_CONTENT, GROUP_ID), groupId)) {
			getDb().executeUpdate("delete from " + TABLE_NAME + " where " + ID + " = ?", groupId);
			getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = null, {2} = null where {1} = ?",
															Idees.TABLE_NAME,
															IdeeColumns.GROUPE_KDO_ID,
															IdeeColumns.RESERVE_LE),
									groupId);
			return false;
		}
		return true;
	}

	public void removeUserFromAllGroups(int userId) throws SQLException {

		String query = MessageFormat.format("select distinct {0} from {1} where {2} = ? ", GROUP_ID, TABLE_NAME_CONTENT, USER_ID);

		try (PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query)) {

			ps.bindParameters(userId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					removeUserFromGroup(userId, res.getInt(GROUP_ID.name()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(MessageFormat.format(	"Error while fetching query to remove user id {0} from groups. Exception: {1}",
												userId,
												e.getMessage()));
		}

	}

	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @return True if and only if this user belongs to this group.
	 * @throws SQLException
	 */
	public boolean belongsToGroup(int userId, int groupId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME_CONTENT,
																USER_ID,
																GROUP_ID),
										userId,
										groupId);
	}

}
