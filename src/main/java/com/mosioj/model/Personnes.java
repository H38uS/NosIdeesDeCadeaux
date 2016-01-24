package com.mosioj.model;

import java.sql.SQLException;

import com.mosioj.utils.database.InternalConnection;

/**
 * Représente la table de personnes.
 * 
 * @author Jordan Mosio
 *
 */
public class Personnes {

	/**
	 * The singleton instance.
	 */
	private static Personnes instance;

	/**
	 * Internal constructor.
	 */
	private Personnes() {
		// forbidden
	}

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static Personnes getInstance() {
		if (instance == null) {
			instance = new Personnes();
		}
		return instance;
	}

	/**
	 * Inserts a new person into the database !
	 * 
	 * @param name
	 * @param digestedPwd
	 * @param email
	 * @throws SQLException
	 */
	public void addNewPersonne(String name, String digestedPwd, String email) throws SQLException {
		InternalConnection.executeUpdate("insert into personnes (login, password, email) values (?, ?, ?)", name, digestedPwd, email);
		InternalConnection.executeUpdate("insert into user_roles (login, role) values (?, ?)", name, "user");
	}

}
