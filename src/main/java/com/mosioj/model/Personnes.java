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
	 * @param email
	 * @param digestedPwd
	 * @throws SQLException
	 */
	public void addNewPersonne(String email, String digestedPwd) throws SQLException {
		InternalConnection.executeUpdate("insert into personnes (email, password) values (?, ?)", email, digestedPwd);
		InternalConnection.executeUpdate("insert into user_roles (login, role) values (?, ?)", email, "user");
	}

}
