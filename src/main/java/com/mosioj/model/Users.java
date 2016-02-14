package com.mosioj.model;

import java.sql.SQLException;

import com.mosioj.utils.database.InternalConnection;

/**
 * Représente la table de personnes.
 * 
 * @author Jordan Mosio
 *
 */
public class Users {

	/**
	 * The singleton instance.
	 */
	private static Users instance;

	/**
	 * Internal constructor.
	 */
	private Users() {
		// forbidden
	}

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static Users getInstance() {
		if (instance == null) {
			instance = new Users();
		}
		return instance;
	}

	/**
	 * Inserts a new person into the database !
	 * 
	 * @param email
	 * @param digestedPwd
	 * @throws SQLException
	 */
	public void addNewPersonne(String email, String digestedPwd) throws SQLException {
		InternalConnection.executeUpdate("insert into users (email, password, creation_date) values (?, ?, now())", email, digestedPwd);
		InternalConnection.executeUpdate("insert into user_roles (email, role) values (?, ?)", email, "user");
	}

	/**
	 * 
	 * @param name The identifier of the person (currently the email).
	 * @return This person's id.
	 * @throws SQLException
	 */
	public int getId(String name) throws SQLException {
		return InternalConnection.selectInt("select id from users where email = ?", name);
	}

}
