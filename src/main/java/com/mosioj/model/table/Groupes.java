package com.mosioj.model.table;

import static com.mosioj.model.table.columns.GroupesKDOColumns.CREATION_DATE;
import static com.mosioj.model.table.columns.GroupesKDOColumns.ID;
import static com.mosioj.model.table.columns.GroupesKDOColumns.NAME;
import static com.mosioj.model.table.columns.GroupesKDOColumns.OWNER_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Group;
import com.mosioj.model.GroupAdmin;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.GroupeJoinRequestsColumns;
import com.mosioj.model.table.columns.GroupeKDOMembersColumn;
import com.mosioj.model.table.columns.GroupesAdminColumns;
import com.mosioj.model.table.columns.GroupesKDOColumns;
import com.mosioj.utils.database.DataSourceIdKDo;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class Groupes extends Table {

	private static final Logger LOGGER = LogManager.getLogger(Groupes.class);

	public static final String TABLE_NAME = "GROUPES_KDO";
	public static final String GROUPE_MEMBERS = "GROUPES_KDO_MEMBERS";
	public static final String GROUPES_ADMIN = "GROUPES_ADMIN";

	private static final String ALREADY_IN = "CONCAT('Vous faites déjà parti de ce groupe ! <a href=\"protected/mes_listes?group=',g.ID,'\" >Voir les idées de ce groupe.</a>')";

	/**
	 * 
	 * @param nameToMatch
	 * @return All group names matching the given string.
	 * @throws SQLException
	 */
	public List<Group> getGroupsToJoin(String nameToMatch, int userId) throws SQLException {

		nameToMatch = nameToMatch.replaceAll("!", "!!");
		nameToMatch = nameToMatch.replaceAll("%", "!%");
		nameToMatch = nameToMatch.replaceAll("_", "!_");
		nameToMatch = nameToMatch.replaceAll("\\[", "![");

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("select g.{0}, g.{1}, count(*),", ID, NAME));

		query.append(" ( select ").append(ALREADY_IN).append(" as status from ");
		query.append(MessageFormat.format(	"{0} gm2 where gm2.{1} = ? and gm2.{2} = g.{3} ) ",
											GROUPE_MEMBERS,
											GroupeKDOMembersColumn.USER_ID,
											GroupeKDOMembersColumn.GROUPE_ID,
											ID));

		query.append(MessageFormat.format("from {0} g, {1} gm ", TABLE_NAME, GROUPE_MEMBERS));
		query.append(MessageFormat.format("where g.{0} = gm.{1} ", ID, GroupeKDOMembersColumn.GROUPE_ID));
		query.append(MessageFormat.format("and g.{0} like ? ESCAPE ''!'' ", NAME));
		query.append(MessageFormat.format("group by g.{0}", ID));

		List<Group> groupes = new ArrayList<Group>();
		LOGGER.debug(query);
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters(userId, "%" + nameToMatch + "%");

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				groupes.add(new Group(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4)));
			}

		} finally {
			ps.close();
		}

		return groupes;
	}

	/**
	 * 
	 * @param userId The user id.
	 * @param groupId The group ID to filter, if positive.
	 * @return All groups to which the user belongs to.
	 * @throws SQLException
	 */
	public List<Group> getGroupsJoined(int userId, int groupId) throws SQLException {

		LOGGER.debug("Getting all groups that the user belongs to...");
		DataSourceIdKDo db = getDb();

		StringBuilder query = new StringBuilder();
		query.append("select ");
		query.append(MessageFormat.format("gm.{0}, g.{1}", GroupeKDOMembersColumn.GROUPE_ID, GroupesKDOColumns.NAME));
		query.append(MessageFormat.format(" from {0}", GROUPE_MEMBERS));
		query.append(" gm ");
		query.append(MessageFormat.format(	"left join {0} g on gm.{1} = g.{2}",
											TABLE_NAME,
											GroupeKDOMembersColumn.GROUPE_ID,
											GroupesKDOColumns.ID));
		query.append(MessageFormat.format(" where {0} = ? ", GroupeKDOMembersColumn.USER_ID));

		if (groupId > 0) {
			query.append(MessageFormat.format(" and gm.{0} = ? ", GroupeKDOMembersColumn.GROUPE_ID));
		}

		List<Group> groupes = new ArrayList<Group>();
		LOGGER.trace("Building query: " + query.toString());
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(db, query.toString());
		try {
			if (groupId > 0) {
				ps.bindParameters(userId, groupId);
			} else {
				ps.bindParameters(userId);
			}

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				groupes.add(new Group(res.getInt(1), res.getString(2), -1, null));
			}

		} finally {
			ps.close();
		}

		return groupes;
	}

	/**
	 * 
	 * @param userId The user id.
	 * @return True if the given user has already created a group.
	 * @throws SQLException
	 */
	public boolean hasAGroup(int userId) throws SQLException {
		return getDb().selectInt(	MessageFormat.format(	"select count(*) from {0} where {1} = ?",
															TABLE_NAME,
															OWNER_ID),
									userId) > 0;
	}

	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @return True if and only if the user is the owner of this group.
	 * @throws SQLException
	 */
	public boolean isGroupOwner(int userId, int groupId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME,
																OWNER_ID,
																ID),
										userId,
										groupId);
	}

	/**
	 * Crée un groupe.
	 * 
	 * @param groupeName
	 * @param userId
	 * @throws SQLException
	 */
	public void createGroup(String groupeName, int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1}, {2}, {3}) values (?, ?, now())",
														TABLE_NAME,
														NAME,
														OWNER_ID,
														CREATION_DATE),
								groupeName,
								userId);
		int groupeId = getDb().selectInt(	MessageFormat.format(	"Select {0} from {1} where {2} = ?",
																	ID,
																	TABLE_NAME,
																	OWNER_ID),
											userId);
		addAssociation(groupeId, userId);
		addAdmin(groupeId, userId);
	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @return True if and only if userId belongs to groupId.
	 * @throws SQLException
	 */
	public boolean associationExists(int groupId, int userId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																GROUPE_MEMBERS,
																GroupeKDOMembersColumn.GROUPE_ID,
																GroupeKDOMembersColumn.USER_ID),
										groupId,
										userId);
	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @return True if and only if userId is an admin of group groupId.
	 * @throws SQLException
	 */
	public boolean isAdminOf(int groupId, int userId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																GROUPES_ADMIN,
																GroupesAdminColumns.GROUPE_ID,
																GroupesAdminColumns.ADMIN),
										groupId,
										userId);
	}

	/**
	 * Adds a new association between a group and a member.
	 * 
	 * @param groupeId
	 * @param userId
	 * @throws SQLException
	 */
	public void addAssociation(int groupeId, int userId) throws SQLException {
		SQLException exception = null;
		try {
			getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1}, {2}, {3}) values (?, ?, now())",
															GROUPE_MEMBERS,
															GroupeKDOMembersColumn.GROUPE_ID,
															GroupeKDOMembersColumn.USER_ID,
															GroupeKDOMembersColumn.JOIN_DATE),
									groupeId,
									userId);
		} catch (SQLException e) {
			exception = e;
		}
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
														GroupeJoinRequests.TABLE_NAME,
														GroupeJoinRequestsColumns.JOINER_ID,
														GroupeJoinRequestsColumns.GROUPE_ID),
								userId,
								groupeId);

		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * 
	 * @param groupId
	 * @return The group name for this id.
	 * @throws SQLException
	 */
	public String getName(int groupId) throws SQLException {
		return getDb().selectString(MessageFormat.format("select {0} from {2} where {1} = ?", NAME, ID, TABLE_NAME),
									groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return The group id for this user, or null if he has no group.
	 * @throws SQLException
	 */
	public int getOwnerGroupId(int userId) throws SQLException {
		// TODO : ajouter la possibilité d'avoir plusieurs groupes
		return getDb().selectInt(	MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, OWNER_ID),
									userId);
	}

	/**
	 * 
	 * @param userId
	 * @return Groups that the user can administriate. Does not include its original group.
	 */
	public List<Integer> getGroupsToAdmin(int userId) {

		List<Integer> groups = new ArrayList<Integer>();

		String query = MessageFormat.format("select {0} from {1} where {2} = ?",
											GroupesAdminColumns.GROUPE_ID,
											GROUPES_ADMIN,
											GroupesAdminColumns.ADMIN);

		PreparedStatementIdKdo ps = null;
		try {
			ps = new PreparedStatementIdKdo(getDb(), query);
			ps.bindParameters(userId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					groups.add(res.getInt(1));
				}
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (ps != null)
				ps.close();
		}

		return groups;
	}

	/**
	 * 
	 * @param groupId
	 * @return All members to the given group.
	 * @throws SQLException
	 */
	public List<User> getUsers(int groupId) throws SQLException {

		LOGGER.debug("Getting the user for the group id " + groupId + ".");
		List<User> users = new ArrayList<User>();

		String query = MessageFormat.format("select gm.{0} from {1} gm where gm.{2} = ?",
											GroupeKDOMembersColumn.USER_ID,
											GROUPE_MEMBERS,
											GroupeKDOMembersColumn.GROUPE_ID);
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query);
		try {
			ps.bindParameters(groupId);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				users.add(new User(res.getInt(1)));
			}

		} finally {
			ps.close();
		}
		return users;
	}

	/**
	 * 
	 * @return The query to check whether there are multiple groups or not
	 */
	public String getExistQuery() {
		return MessageFormat.format("select count(*) from {0} where upper({1}) = upper(?)", TABLE_NAME, NAME);
	}

	/**
	 * Adds a new administrator to this group.
	 * 
	 * @param groupId The group ID.
	 * @param userId The new admin to add.
	 * @throws SQLException
	 */
	public void addAdmin(int groupId, int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1}, {2}) values (?, ?)",
														GROUPES_ADMIN,
														GroupesAdminColumns.GROUPE_ID,
														GroupesAdminColumns.ADMIN),
								groupId,
								userId);
	}

	/**
	 * Removes an administrator from this group.
	 * 
	 * @param groupId The group ID.
	 * @param userId The former admin to remove.
	 * @throws SQLException
	 */
	public void removeAdmin(int groupId, int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
		                      	                     	GROUPES_ADMIN,
		                      	                     	GroupesAdminColumns.GROUPE_ID,
		                      	                     	GroupesAdminColumns.ADMIN),
		                      	groupId,
		                      	userId);
	}

	/**
	 * 
	 * @return The list of administrator of this group.
	 * @throws SQLException
	 */
	public List<GroupAdmin> getAdmins(int groupId) throws SQLException {

		List<GroupAdmin> admins = new ArrayList<GroupAdmin>();
		StringBuilder query = new StringBuilder();
		query.append(" select g.id, a.admin, if(a.admin=g.owner_id,'T','F') ");
		query.append(" from groupes_kdo g, groupes_admin a ");
		query.append(" where g.id = a.groupe_id");
		query.append("   and g.id = ?");
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());

		try {
			ps.bindParameters(groupId);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				admins.add(new GroupAdmin(res.getInt(1), res.getInt(2), "T".equals(res.getString(3))));
			}

		} finally {
			ps.close();
		}
		return admins;
	}
}
