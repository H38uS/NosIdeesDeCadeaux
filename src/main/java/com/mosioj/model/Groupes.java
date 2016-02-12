package com.mosioj.model;

import java.sql.SQLException;

import com.mosioj.utils.database.InternalConnection;

/**
 * Handle group of users.
 * 
 * @author Jordan Mosio
 *
 */
public class Groupes {

	/**
	 * The singleton instance.
	 */
	private static Groupes instance;

	/**
	 * Internal constructor.
	 */
	private Groupes() {
		// forbidden
	}

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static Groupes getInstance() {
		if (instance == null) {
			instance = new Groupes();
		}
		return instance;
	}

	/**
	 * 
	 * @param userId The user id.
	 * @return True if the given user has already created a group.
	 * @throws SQLException 
	 */
	public boolean hasAGroup(int userId) throws SQLException {
		return InternalConnection.selectInt("select count(*) from groupes where owner_id = ?", userId) > 0;
	}

	/**
	 * Crée un groupe.
	 * 
	 * @param groupeName
	 * @param userId
	 * @throws SQLException
	 */
	public void createGroup(String groupeName, int userId) throws SQLException {
		InternalConnection.executeUpdate("insert into groupes (name, owner_id) values (?, ?)", groupeName, userId);
		int groupeId = InternalConnection.selectInt("Select id from groupes where owner_id = ?", userId);
		InternalConnection.executeUpdate("insert into groupes_members (groupe_id, user_id) values (?, ?)", groupeId, userId);
	}
}
