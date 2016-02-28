package com.mosioj.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.utils.database.InternalConnection;

/**
 * Handle group of users.
 * 
 * @author Jordan Mosio
 *
 */
public class Groupe {

	public static final String TABLE_NAME = "GROUPES";

	private final int id;
	private final String name;
	private final int nbMembers;
	private final String status;

	/**
	 * Internal constructor.
	 * 
	 * @param pId
	 * @param pName
	 * @param pNbMembers
	 * @param pStatus
	 */
	private Groupe(int pId, String pName, int pNbMembers, String pStatus) {
		id = pId;
		name = pName;
		nbMembers = pNbMembers;
		status = pStatus;
	}

	/**
	 * 
	 * @return This group id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return This group name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The number of members belonging to this group.
	 */
	public int getNbMembers() {
		return nbMembers;
	}

	/**
	 * 
	 * @return An optional group status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param nameToMatch
	 * @return All group names matching the given string.
	 * @throws SQLException
	 */
	public static List<Groupe> getGroupsToJoin(String nameToMatch, int userId) throws SQLException {

		Connection con = InternalConnection.getAConnection();

		nameToMatch = nameToMatch.replaceAll("!", "!!");
		nameToMatch = nameToMatch.replaceAll("%", "!%");
		nameToMatch = nameToMatch.replaceAll("_", "!_");
		nameToMatch = nameToMatch.replaceAll("\\[", "![");

		StringBuilder query = new StringBuilder();
		query.append("select g.id, g.name, count(*), ( select 'Vous faites déjà parti de ce groupe !' as status from groupes_members gm2 where gm2.user_id = ? and gm2.groupe_id = g.id ) ");
		query.append("from " + TABLE_NAME + " g, groupes_members gm ");
		query.append("where g.id = gm.groupe_id ");
		query.append("and g.name like ? ESCAPE '!' ");
		query.append("group by g.id");

		List<Groupe> groupes = new ArrayList<Groupe>();
		try {
			PreparedStatement ps = con.prepareStatement(query.toString());
			InternalConnection.bindParameters(ps, userId, "%" + nameToMatch + "%");

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
	 * @return True if the given user has already created a group.
	 * @throws SQLException
	 */
	public static boolean hasAGroup(int userId) throws SQLException {
		return InternalConnection.selectInt("select count(*) from " + TABLE_NAME + " where owner_id = ?", userId) > 0;
	}

	/**
	 * Crée un groupe.
	 * 
	 * @param groupeName
	 * @param userId
	 * @throws SQLException
	 */
	public static void createGroup(String groupeName, int userId) throws SQLException {
		InternalConnection.executeUpdate("insert into " + TABLE_NAME
				+ " (name, owner_id, creation_date) values (?, ?, now())", groupeName, userId);
		int groupeId = InternalConnection.selectInt("Select id from " + TABLE_NAME + " where owner_id = ?", userId);
		addAssociation(groupeId, userId);
	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @return True if and only if userId belongs to groupId.
	 * @throws SQLException
	 */
	public static boolean associationExists(int groupId, int userId) throws SQLException {
		return InternalConnection.doesReturnRows(	"select 1 from groupes_members where user_id = ? and groupe_id = ?",
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
	public static void addAssociation(int groupeId, int userId) throws SQLException {
		InternalConnection.executeUpdate(	"insert into groupes_members (groupe_id, user_id, join_date) values (?, ?, now())",
											groupeId,
											userId);
	}

	/**
	 * 
	 * @param groupId
	 * @return The group name for this id.
	 * @throws SQLException
	 */
	public static String getName(int groupId) throws SQLException {
		return InternalConnection.selectString("select name from groupes where id = ?", groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return The group id for this user, or null if he has no group.
	 * @throws SQLException
	 */
	public static int getGroupId(int userId) throws SQLException {
		return InternalConnection.selectInt("select id from " + TABLE_NAME + " where owner_id = ?", userId);
	}

	/**
	 * 
	 * @param groupId
	 * @return All members to the given group.
	 * @throws SQLException 
	 */
	public static List<User> getUsers(int groupId) throws SQLException {

		List<User> users = new ArrayList<User>();
		Connection con = InternalConnection.getAConnection();

		try {
			String query = "select gm.user_id from groupes_members gm where gm.groupe_id = ?";
			PreparedStatement ps = con.prepareStatement(query);
			InternalConnection.bindParameters(ps, groupId);

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
