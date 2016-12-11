package com.mosioj.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mosioj.utils.database.ConnectionIdKDo;

public class User {

	public final int id;
	private String email;
	private String name;
	private final List<Idee> ideas = new ArrayList<Idee>();
	
	public User(int i) {
		id = i;
		try {
			ConnectionIdKDo db = new ConnectionIdKDo();
			name = db.selectString("select name from users where id = ?", i);
			email = db.selectString("select email from users where id = ?", i);
		} catch (SQLException e) {
			name = "L'identifiant n'existe pas";
		}
	}

	/**
	 * 
	 * @return The id of the person.
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return The email of the person.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @return The name of the person.
	 */
	public String getName() {
		return name;
	}

	public void addIdeas(List<Idee> ownerIdeas) {
		ideas.addAll(ownerIdeas);
	}
	
	public List<Idee> getIdeas() {
		return ideas;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
