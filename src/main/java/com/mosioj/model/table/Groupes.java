package com.mosioj.model.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.model.Groupe;
import com.mosioj.model.User;
import com.mosioj.utils.database.InternalConnection;

public class Groupes {

	public static final String TABLE_NAME = "GROUPES";
	private static Groupes instance;

	private Groupes() {
		// Forbidden
	}

	public static Groupes getGroupesManager() {
		if (instance == null) {
			instance = new Groupes();
		}
		return instance;
	}

	/**
	 * 
	 * @param nameToMatch
	 * @return All group names matching the given string.
	 * @throws SQLException
	 */
	public List<Groupe> getGroupsToJoin(String nameToMatch, int userId) throws SQLException {

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
	public boolean hasAGroup(int userId) throws SQLException {
		return InternalConnection.selectInt("select count(*) from " + TABLE_NAME + " where owner_id = ?", userId) > 0;
	}
	
	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @return True if and only if the user is the owner of this group.
	 * @throws SQLException 
	 */
	public boolean isGroupOwner(int userId, int groupId) throws SQLException {
		// TODO utiliser des colonnes
		return InternalConnection.doesReturnRows("select 1 from " + TABLE_NAME + " where owner_id = ? and id = ?", userId, groupId);
	}

	/**
	 * Crée un groupe.
	 * 
	 * @param groupeName
	 * @param userId
	 * @throws SQLException
	 */
	public void createGroup(String groupeName, int userId) throws SQLException {
		InternalConnection.executeUpdate("insert into " + TABLE_NAME
				+ " (name, owner_id, creation_date) values (?, ?, now())", groupeName, userId);
		int groupeId = InternalConnection.selectInt("Select id from " + TABLE_NAME + " where owner_id = ?", userId);
		getGroupesManager().addAssociation(groupeId, userId);
	}

	/**
	 * 
	 * @param groupId
	 * @param userId
	 * @return True if and only if userId belongs to groupId.
	 * @throws SQLException
	 */
	public boolean associationExists(int groupId, int userId) throws SQLException {
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
	public void addAssociation(int groupeId, int userId) throws SQLException {
		InternalConnection.executeUpdate(	"insert into groupes_members (groupe_id, user_id, join_date) values (?, ?, now())",
											groupeId,
											userId);
		InternalConnection.executeUpdate(MessageFormat.format(	"delete from {0} where {1} = ? and {2} = ?",
																GroupeJoinRequests.TABLE_NAME,
																GroupeJoinRequestsColumns.JOINER_ID,
																GroupeJoinRequestsColumns.GROUPE_ID),
											userId, groupeId);
	}

	/**
	 * 
	 * @param groupId
	 * @return The group name for this id.
	 * @throws SQLException
	 */
	public String getName(int groupId) throws SQLException {
		return InternalConnection.selectString("select name from groupes where id = ?", groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return The group id for this user, or null if he has no group.
	 * @throws SQLException
	 */
	public int getGroupId(int userId) throws SQLException {
		return InternalConnection.selectInt("select id from " + TABLE_NAME + " where owner_id = ?", userId);
	}

	/**
	 * 
	 * @param groupId
	 * @return All members to the given group.
	 * @throws SQLException
	 */
	public List<User> getUsers(int groupId) throws SQLException {

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
