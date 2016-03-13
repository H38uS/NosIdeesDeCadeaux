package com.mosioj.model.table;

import static com.mosioj.model.table.GroupeJoinRequestsColumns.GROUPE_ID;
import static com.mosioj.model.table.GroupeJoinRequestsColumns.JOINER_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.model.Demande;
import com.mosioj.utils.database.InternalConnection;

public class GroupeJoinRequests {

	public static final String TABLE_NAME = "GROUPE_JOIN_REQUESTS";
	private static GroupeJoinRequests instance;

	public static GroupeJoinRequests getManager() {
		if (instance == null) {
			instance = new GroupeJoinRequests();
		}
		return instance;
	}
	
	/**
	 * Insert a new line.
	 * 
	 * @param joinerId
	 * @param groupeId
	 * @throws SQLException
	 */
	public void insert(int joinerId, int groupeId) throws SQLException {

		StringBuilder insert = new StringBuilder();
		insert.append("insert into " + TABLE_NAME + " ");
		insert.append("(" + JOINER_ID + "," + GROUPE_ID + ") ");
		insert.append(" values ");
		insert.append("(?, ?)");

		InternalConnection.executeUpdate(insert.toString(), joinerId, groupeId);
	}

	/**
	 * 
	 * @param joinerId
	 * @param groupeId
	 * @return True if an association already exists.
	 * @throws SQLException
	 */
	public boolean associationExists(int joinerId, int groupeId) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append("select 1 from " + TABLE_NAME + " ");
		query.append("where " + JOINER_ID + " = ? and ");
		query.append(GROUPE_ID + " = ?");

		return InternalConnection.doesReturnRows(query.toString(), joinerId, groupeId);
	}

	/**
	 * Deletes a previously made request. Does nothing if no request has been made.
	 * 
	 * @param groupId
	 * @param userId
	 * @throws SQLException
	 */
	public void cancelRequest(int groupId, int userId) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append("delete from " + TABLE_NAME + " ");
		query.append("where " + JOINER_ID + " = ? and ");
		query.append(GROUPE_ID + " = ?");
		
		InternalConnection.executeUpdate(query.toString(), userId, groupId);
	}

	/**
	 * 
	 * @param groupId
	 * @return The list of users that ask to join this group.
	 * @throws SQLException
	 */
	public List<Demande> getDemandes(int groupId) throws SQLException {

		List<Demande> demandes = new ArrayList<Demande>();
		Connection con = InternalConnection.getAConnection();

		try {
			StringBuilder query = new StringBuilder();
			query.append("select " + JOINER_ID + ", COALESCE(u.name, u.email) as name ");
			query.append("  from " + TABLE_NAME + " ");
			query.append(" inner join " + Users.TABLE_NAME + " u on " + JOINER_ID + " = u.id ");
			query.append(" where " + GROUPE_ID + " = ?");
			
			PreparedStatement ps = con.prepareStatement(query.toString(), groupId);
			InternalConnection.bindParameters(ps, groupId);

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				demandes.add(new Demande(res.getInt(1), res.getString(2)));
			}

		} finally {
			con.close();
		}

		return demandes;
	}
}
