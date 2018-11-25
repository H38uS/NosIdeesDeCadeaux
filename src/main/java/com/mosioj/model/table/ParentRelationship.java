package com.mosioj.model.table;

import static com.mosioj.model.table.columns.ParentRelationshipColumns.ID;
import static com.mosioj.model.table.columns.ParentRelationshipColumns.CHILD_ID;
import static com.mosioj.model.table.columns.ParentRelationshipColumns.PARENT_ID;
import static com.mosioj.model.table.columns.ParentRelationshipColumns.CREATION_DATE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.model.User;
import com.mosioj.model.table.columns.UsersColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;

public class ParentRelationship extends Table {

	public static final String TABLE_NAME = "PARENT_RELATIONSHIP";

	/**
	 * 
	 * @param parentId
	 * @return Tous les comptes qui sont gérés par procuration
	 * @throws SQLException
	 */
	public List<User> getChildren(int parentId) throws SQLException {

		List<User> users = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format(	" select u.{0}, u.{1}, u.{2}, u.{3} ",
										ID,
										UsersColumns.NAME,
										UsersColumns.EMAIL,
										UsersColumns.AVATAR));
		sb.append(MessageFormat.format("   from {0} t ", TABLE_NAME));
		sb.append(MessageFormat.format("   left join {0} u", Users.TABLE_NAME));
		sb.append(MessageFormat.format("     on u.{0} = t.{1}", UsersColumns.ID, CHILD_ID));
		sb.append(MessageFormat.format("  where t.{0} = ?", PARENT_ID));

		PreparedStatementIdKdo ps = null;

		try {
			ps = new PreparedStatementIdKdo(getDb(), sb.toString());
			ps.bindParameters(parentId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					users.add(new User(	res.getInt(ID.name()),
										res.getString(UsersColumns.NAME.name()),
										res.getString(UsersColumns.EMAIL.name()),
										res.getString(UsersColumns.AVATAR.name())));
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
	 * @param childId
	 * @return Les détenants de la procuration. Peut être vide.
	 * @throws SQLException
	 */
	public List<User> getParents(int childId) throws SQLException {

		List<User> users = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format(	" select u.{0}, u.{1}, u.{2}, u.{3} ",
										ID,
										UsersColumns.NAME,
										UsersColumns.EMAIL,
										UsersColumns.AVATAR));
		sb.append(MessageFormat.format("   from {0} t ", TABLE_NAME));
		sb.append(MessageFormat.format("   left join {0} u", Users.TABLE_NAME));
		sb.append(MessageFormat.format("     on u.{0} = t.{1}", UsersColumns.ID, PARENT_ID));
		sb.append(MessageFormat.format("  where t.{0} = ?", CHILD_ID));

		PreparedStatementIdKdo ps = null;

		try {
			ps = new PreparedStatementIdKdo(getDb(), sb.toString());
			ps.bindParameters(childId);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					users.add(new User(	res.getInt(ID.name()),
										res.getString(UsersColumns.NAME.name()),
										res.getString(UsersColumns.EMAIL.name()),
										res.getString(UsersColumns.AVATAR.name())));
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
	 * @param parentId
	 * @param childId
	 * @return True if and only if this relation already exists.
	 * @throws SQLException
	 */
	public boolean doesRelationExists(int parentId, int childId) throws SQLException {
		return getDb().doesReturnRows(	MessageFormat.format(	"select 1 from {0} where {1} = ? and {2} = ?",
																TABLE_NAME,
																PARENT_ID,
																CHILD_ID),
										parentId,
										childId);
	}

	/**
	 * Deletes all the parents of the given child if it exists.
	 * 
	 * @param childId
	 * @throws SQLException
	 */
	public void deleteParents(int childId) throws SQLException {
		getDb().executeUpdate(MessageFormat.format("delete from {0} where {1} = ?", TABLE_NAME, CHILD_ID), childId);
	}

	/**
	 * 
	 * @param parentId
	 * @param childId
	 * @throws SQLException
	 */
	public void addProcuration(int parentId, int childId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1},{2},{3}) values (?, ?, now())",
														TABLE_NAME,
														PARENT_ID,
														CHILD_ID,
														CREATION_DATE),
								parentId,
								childId);
	}

	public void deleteAllRelationForUser(int userId) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format("delete from {0} where {1} = ? or {2} = ?", TABLE_NAME, PARENT_ID, CHILD_ID),
								userId,
								userId);
	}
}
