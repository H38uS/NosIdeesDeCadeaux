package com.mosioj.model;

import java.sql.SQLException;

import com.mosioj.utils.database.InternalConnection;

public class User {

	private String name;
	
	public User(int i) {
		try {
			name = InternalConnection.selectString("select email from users where id = ?", i);
		} catch (SQLException e) {
			name = "L'identifiant n'existe pas";
		}
	}

	/**
	 * 
	 * @return The name of the person.
	 */
	public String getName() {
		return name;
	}
}
