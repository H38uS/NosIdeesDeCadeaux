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
	 * Internal constructor.
	 */
	private Users() {
		// forbidden
	}

	/**
	 * Inserts a new person into the database !
	 * 
	 * @param email
	 * @param digestedPwd
	 * @throws SQLException
	 */
	public static void addNewPersonne(String email, String digestedPwd) throws SQLException {
		InternalConnection.executeUpdate("insert into users (email, password, creation_date) values (?, ?, now())", email, digestedPwd);
		InternalConnection.executeUpdate("insert into user_roles (email, role) values (?, ?)", email, "user");
	}

	/**
	 * 
	 * @param name The identifier of the person (currently the email).
	 * @return This person's id.
	 * @throws SQLException
	 */
	public static int getId(String name) throws SQLException {
		return InternalConnection.selectInt("select id from users where email = ?", name);
	}

}
