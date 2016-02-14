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

	/**
	 * Internal constructor.
	 */
	private Groupe(int pId, String pName, int pNbMembers) {
		id = pId;
		name = pName;
		nbMembers = pNbMembers;
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
	 * @param nameToMatch
	 * @return All group names matching the given string.
	 * @throws SQLException
	 */
	public static List<Groupe> getGroupe(String nameToMatch) throws SQLException {

		Connection con = InternalConnection.getAConnection();

		nameToMatch = nameToMatch.replaceAll("!", "!!");
		nameToMatch = nameToMatch.replaceAll("%", "!%");
		nameToMatch = nameToMatch.replaceAll("_", "!_");
		nameToMatch = nameToMatch.replaceAll("\\[", "![");

		StringBuilder query = new StringBuilder();
		query.append("select g.id, g.name, count(*) ");
		query.append("from " + TABLE_NAME + " g, groupes_members gm ");
		query.append("where g.id = gm.groupe_id ");
		query.append("and g.name like ? ESCAPE '!' ");
		query.append("group by g.id");

		List<Groupe> groupes = new ArrayList<Groupe>();
		try {
			PreparedStatement ps = con.prepareStatement(query.toString());
			InternalConnection.bindParameters(ps, "%" + nameToMatch + "%");

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				groupes.add(new Groupe(res.getInt(1), res.getString(2), res.getInt(3)));
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
		InternalConnection.executeUpdate(	"insert into groupes_members (groupe_id, user_id, join_date) values (?, ?, now())",
											groupeId,
											userId);
	}
}
