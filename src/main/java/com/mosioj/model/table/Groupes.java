package com.mosioj.model.table;

import static com.mosioj.model.table.columns.GroupesKDOColumns.CREATION_DATE;
import static com.mosioj.model.table.columns.GroupesKDOColumns.ID;
import static com.mosioj.model.table.columns.GroupesKDOColumns.NAME;
import static com.mosioj.model.table.columns.GroupesKDOColumns.OWNER_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Groupe;
import com.mosioj.model.User;
import com.mosioj.model.table.columns.GroupeJoinRequestsColumns;
import com.mosioj.model.table.columns.GroupeKDOMembersColumn;
import com.mosioj.model.table.columns.GroupesKDOColumns;
import com.mosioj.utils.database.ConnectionIdKDo;

public class Groupes extends Table {

	private static final Logger LOGGER = LogManager.getLogger(Groupes.class);

	public static final String TABLE_NAME = "GROUPES_KDO";
	public static final String GROUPE_MEMBERS = "GROUPES_KDO_MEMBERS";

	/**
	 * 
	 * @param nameToMatch
	 * @return All group names matching the given string.
	 * @throws SQLException
	 */
	public List<Groupe> getGroupsToJoin(String nameToMatch, int userId) throws SQLException {

		Connection con = getDb().getAConnection();

		nameToMatch = nameToMatch.replaceAll("!", "!!");
		nameToMatch = nameToMatch.replaceAll("%", "!%");
		nameToMatch = nameToMatch.replaceAll("_", "!_");
		nameToMatch = nameToMatch.replaceAll("\\[", "![");

		StringBuilder query = new StringBuilder();
		query.append(MessageFormat.format("select g.{0}, g.{1}, count(*),", ID, NAME));

		query.append(" ( select 'Vous faites déjà parti de ce groupe !' as status from ");
		query.append(MessageFormat.format(	"{0} gm2 where gm2.{1} = ? and gm2.{2} = g.{3} ) ",
											GROUPE_MEMBERS,
											GroupeKDOMembersColumn.USER_ID,
											GroupeKDOMembersColumn.GROUPE_ID,
											ID));

		query.append(MessageFormat.format("from {0} g, {1} gm ", TABLE_NAME, GROUPE_MEMBERS));
		query.append(MessageFormat.format("where g.{0} = gm.{1} ", ID, GroupeKDOMembersColumn.GROUPE_ID));
		query.append(MessageFormat.format("and g.{0} like ? ESCAPE ''!'' ", NAME));
		query.append(MessageFormat.format("group by g.{0}", ID));

		List<Groupe> groupes = new ArrayList<Groupe>();
		try {
			// FIXME close le preparestatement, pas la connexion
			PreparedStatement ps = con.prepareStatement(query.toString());
			getDb().bindParameters(ps, userId, "%" + nameToMatch + "%");

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				groupes.add(new Groupe(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4)));
			}

		} finally {
			con.close();
		}

		return groupes;
	}

	/**
	 * 
	 * @param userId The user id.
	 * @return All groups to which the user belongs to.
	 * @throws SQLException
	 */
	public List<Groupe> getGroupsJoined(int userId) throws SQLException {

		ConnectionIdKDo db = getDb();
		Connection con = db.getAConnection();

		StringBuilder query = new StringBuilder();
		query.append("select ");
		query.append(MessageFormat.format("gm.{0}, g.{1}", GroupeKDOMembersColumn.GROUPE_ID, GroupesKDOColumns.NAME));
		query.append(MessageFormat.format(" from {0}", GROUPE_MEMBERS));
		query.append(" gm ");
		query.append(MessageFormat.format(	"left join {0} g on gm.{1} = g.{2}",
											TABLE_NAME,
											GroupeKDOMembersColumn.GROUPE_ID,
											GroupesKDOColumns.ID));
		query.append(MessageFormat.format(" where {0} = ?", GroupeKDOMembersColumn.USER_ID));

		List<Groupe> groupes = new ArrayList<Groupe>();
		try {
			LOGGER.trace("Building query: " + query.toString());
			PreparedStatement ps = con.prepareStatement(query.toString());
			db.bindParameters(ps, userId);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				groupes.add(new Groupe(res.getInt(1), res.getString(2), -1, null));
			}

		} finally {
			con.close();
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
																GroupeKDOMembersColumn.USER_ID,
																GroupeKDOMembersColumn.GROUPE_ID),
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
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1}, {2}, {3}) values (?, ?, now())",
														GROUPE_MEMBERS,
														GroupeKDOMembersColumn.GROUPE_ID,
														GroupeKDOMembersColumn.USER_ID,
														GroupeKDOMembersColumn.JOIN_DATE),
								groupeId,
								userId);
		getDb().executeUpdate(	MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
														GroupeJoinRequests.TABLE_NAME,
														GroupeJoinRequestsColumns.JOINER_ID,
														GroupeJoinRequestsColumns.GROUPE_ID),
								userId,
								groupeId);
	}

	/**
	 * 
	 * @param groupId
	 * @return The group name for this id.
	 * @throws SQLException
	 */
	public String getName(int groupId) throws SQLException {
		return getDb().selectString(MessageFormat.format("select {0} from groupes where {1} = ?", NAME, ID), groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return The group id for this user, or null if he has no group.
	 * @throws SQLException
	 */
	public int getOwnerGroupId(int userId) throws SQLException {
		// FIXME : ajouter la possibilité d'avoir plusieurs groupes
		return getDb().selectInt(	MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, OWNER_ID),
									userId);
	}

	/**
	 * 
	 * @param groupId
	 * @return All members to the given group.
	 * @throws SQLException
	 */
	public List<User> getUsers(int groupId) throws SQLException {

		List<User> users = new ArrayList<User>();
		Connection con = getDb().getAConnection();

		try {
			String query = MessageFormat.format("select gm.{0} from {1} gm where gm.{2} = ?",
												GroupeKDOMembersColumn.USER_ID,
												GROUPE_MEMBERS,
												GroupeKDOMembersColumn.GROUPE_ID);
			PreparedStatement ps = con.prepareStatement(query);
			getDb().bindParameters(ps, groupId);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				users.add(new User(res.getInt(1)));
			}

		} finally {
			con.close();
		}
		return users;
	}
}
