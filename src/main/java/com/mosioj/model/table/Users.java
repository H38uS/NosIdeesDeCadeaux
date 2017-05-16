package com.mosioj.model.table;

import static com.mosioj.model.table.columns.UsersColumns.CREATION_DATE;
import static com.mosioj.model.table.columns.UsersColumns.EMAIL;
import static com.mosioj.model.table.columns.UsersColumns.ID;
import static com.mosioj.model.table.columns.UsersColumns.NAME;
import static com.mosioj.model.table.columns.UsersColumns.PASSWORD;
import static com.mosioj.model.table.columns.UsersColumns.BIRTHDAY;
import static com.mosioj.model.table.columns.UsersColumns.AVATAR;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.model.table.columns.UserRolesColumns;
import com.mosioj.utils.database.PreparedStatementIdKdo;

/**
 * Représente la table de personnes.
 * 
 * @author Jordan Mosio
 *
 */
public class Users extends Table {

	public static final String TABLE_NAME = "USERS";
	private static final Logger LOGGER = LogManager.getLogger(Users.class);

	/**
	 * Inserts a new person into the database !
	 * 
	 * @param email
	 * @param digestedPwd
	 * @param name
	 * @throws SQLException
	 */
	public void addNewPersonne(String email, String digestedPwd, String name) throws SQLException {
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into {0} ({1},{2},{3},{4}) values (?, ?, now(), ?)",
																	TABLE_NAME,
																	EMAIL,
																	PASSWORD,
																	CREATION_DATE,
																	NAME),
											email,
											digestedPwd,
											name);
		getDb().executeUpdateGeneratedKey(	MessageFormat.format(	"insert into user_roles ({0},{1}) values (?, ?)",
																	UserRolesColumns.EMAIL,
																	UserRolesColumns.ROLE),
											email,
											"ROLE_USER");
	}

	/**
	 * 
	 * @param id
	 * @return The user corresponding to this ID or null if not found.
	 * @throws SQLException
	 */
	public User getUser(int id) throws SQLException {

		User user = null;
		String query = MessageFormat.format("select {0}, {1}, {2}, {3}, {5} from {4} where {0} = ?",
											ID,
											NAME,
											EMAIL,
											BIRTHDAY,
											TABLE_NAME,
											AVATAR);
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query);
		try {
			ps.bindParameters(id);
			if (ps.execute()) {
				ResultSet res = ps.getResultSet();
				while (res.next()) {
					user = new User(res.getInt(ID.name()),
									res.getString(NAME.name()),
									res.getString(EMAIL.name()),
									res.getDate(BIRTHDAY.name()),
									res.getString(AVATAR.name()));
				}
			}
		} finally {
			ps.close();
		}

		return user;
	}

	/**
	 * 
	 * @param email The identifier of the person (currently the email).
	 * @return This person's id.
	 * @throws SQLException
	 */
	public int getId(String email) throws SQLException {
		return getDb().selectInt(MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, EMAIL), email);
	}

	/**
	 * Persists the user configuration in DB.
	 * 
	 * @param user
	 * @throws SQLException
	 */
	public void update(User user) throws SQLException {
		LOGGER.trace(MessageFormat.format("Updating user {0}. Avatar: {1}", user.id, user.avatar));
		String query = MessageFormat.format("update {0} set {1} = ?, {2} = ?, {3} = ?, {5} = ? where {4} = ?",
											TABLE_NAME,
											EMAIL,
											NAME,
											BIRTHDAY,
											ID,
											AVATAR);
		getDb().executeUpdate(query, user.email, user.name, user.birthday, user.avatar, user.id);
	}

	public List<User> getUsers(String nameToMatch) throws SQLException {

		List<User> users = new ArrayList<User>();
		LOGGER.debug("Getting users from search token: '" + nameToMatch + "'.");

		nameToMatch = nameToMatch.replaceAll("!", "!!");
		nameToMatch = nameToMatch.replaceAll("%", "!%");
		nameToMatch = nameToMatch.replaceAll("_", "!_");
		nameToMatch = nameToMatch.replaceAll("\\[", "![");

		String query = MessageFormat.format("select {0},{1},{2},{6} from {3} where {4} like ? ESCAPE ''!'' or {5} like ? ESCAPE ''!''",
											ID,
											NAME,
											EMAIL,
											TABLE_NAME,
											NAME,
											EMAIL,
											AVATAR);

		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query.toString());
		try {
			ps.bindParameters("%" + nameToMatch + "%", "%" + nameToMatch + "%");

			if (!ps.execute()) {
				throw new SQLException("No result set available.");
			}

			ResultSet res = ps.getResultSet();
			while (res.next()) {
				users.add(new User(	res.getInt(ID.name()),
									res.getString(NAME.name()),
									res.getString(EMAIL.name()),
									res.getString(AVATAR.name())));
			}

		} finally {
			ps.close();
		}

		return users;
	}

}
