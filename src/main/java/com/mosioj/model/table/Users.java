package com.mosioj.model.table;

import static com.mosioj.model.table.columns.UsersColumns.CREATION_DATE;
import static com.mosioj.model.table.columns.UsersColumns.EMAIL;
import static com.mosioj.model.table.columns.UsersColumns.ID;
import static com.mosioj.model.table.columns.UsersColumns.PASSWORD;
import static com.mosioj.model.table.columns.UsersColumns.NAME;

import java.sql.SQLException;
import java.text.MessageFormat;

import com.mosioj.model.User;
import com.mosioj.model.table.columns.UserRolesColumns;

/**
 * Représente la table de personnes.
 * 
 * @author Jordan Mosio
 *
 */
public class Users extends Table {

	public static final String TABLE_NAME = "USERS";

	/**
	 * Inserts a new person into the database !
	 * 
	 * @param email
	 * @param digestedPwd
	 * @param name
	 * @throws SQLException
	 */
	public void addNewPersonne(String email, String digestedPwd, String name) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0} ({1},{2},{3},{4}) values (?, ?, now(), ?)",
														TABLE_NAME,
														EMAIL,
														PASSWORD,
														CREATION_DATE,
														NAME),
								email,
								digestedPwd,
								name);
		// FIXME faire un écran mon compte ?
		getDb().executeUpdate(	MessageFormat.format(	"insert into user_roles ({0},{1}) values (?, ?)",
														UserRolesColumns.EMAIL,
														UserRolesColumns.ROLE),
								email,
								"ROLE_USER");
	}

	/**
	 * 
	 * @param email The identifier of the person (currently the email).
	 * @return This person's id.
	 * @throws SQLException
	 */
	public int getId(String email) throws SQLException {
		return getDb().selectInt(	MessageFormat.format("select {0} from {1} where {2} = ?", ID, TABLE_NAME, EMAIL),
									email);
	}

	/**
	 * Persists the user configuration in DB.
	 * 
	 * @param user
	 * @throws SQLException
	 */
	public void update(User user) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"update {0} set {1} = ?, {2} = ? where {3} = ?",
														TABLE_NAME,
														EMAIL,
														NAME,
														ID),
								user.email,
								user.name,
								user.id);
	}

}
