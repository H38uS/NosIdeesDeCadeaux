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
import com.mosioj.model.table.columns.GroupeJoinRequestsColumns;

public class Groupes extends Table {

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
		query.append("select g.id, g.name, count(*),");

		query.append(" ( select 'Vous faites déjà parti de ce groupe !' as status from ");
		query.append(GROUPE_MEMBERS + " gm2 where gm2.user_id = ? and gm2.groupe_id = g.id ) ");

		query.append("from " + TABLE_NAME + " g, " + GROUPE_MEMBERS + " gm ");
		query.append("where g.id = gm.groupe_id ");
		query.append("and g.name like ? ESCAPE '!' ");
		query.append("group by g.id");

		List<Groupe> groupes = new ArrayList<Groupe>();
		try {
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
	 * @return True if the given user has already created a group.
	 * @throws SQLException
	 */
	public boolean hasAGroup(int userId) throws SQLException {
		return getDb().selectInt("select count(*) from " + TABLE_NAME + " where owner_id = ?", userId) > 0;
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
		return getDb().doesReturnRows(	"select 1 from " + TABLE_NAME + " where owner_id = ? and id = ?",
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
		getDb().executeUpdate(	"insert into " + TABLE_NAME + " (name, owner_id, creation_date) values (?, ?, now())",
								groupeName,
								userId);
		int groupeId = getDb().selectInt("Select id from " + TABLE_NAME + " where owner_id = ?", userId);
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
		return getDb().doesReturnRows(	"select 1 from " + GROUPE_MEMBERS + " where user_id = ? and groupe_id = ?",
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
		getDb().executeUpdate(	"insert into " + GROUPE_MEMBERS
				+ " (groupe_id, user_id, join_date) values (?, ?, now())",
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
		return getDb().selectString("select name from groupes where id = ?", groupId);
	}

	/**
	 * 
	 * @param userId
	 * @return The group id for this user, or null if he has no group.
	 * @throws SQLException
	 */
	public int getGroupId(int userId) throws SQLException {
		return getDb().selectInt("select id from " + TABLE_NAME + " where owner_id = ?", userId);
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
			String query = "select gm.user_id from " + GROUPE_MEMBERS + " gm where gm.groupe_id = ?";
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
