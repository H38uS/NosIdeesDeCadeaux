package com.mosioj.model.table;

import java.sql.SQLException;

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
	 * @throws SQLException
	 */
	public void addNewPersonne(String email, String digestedPwd) throws SQLException {
		getDb().executeUpdate("insert into users (email, password, creation_date) values (?, ?, now())", email, digestedPwd);
		getDb().executeUpdate("insert into user_roles (email, role) values (?, ?)", email, "user");
	}

	/**
	 * 
	 * @param name The identifier of the person (currently the email).
	 * @return This person's id.
	 * @throws SQLException
	 */
	public int getId(String name) throws SQLException {
		return getDb().selectInt("select id from users where email = ?", name);
	}

}
