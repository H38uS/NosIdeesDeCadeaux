package com.mosioj.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class User {

	public final int id;
	public String email;
	public String name;
	public Date birthday;

	private final List<Idee> ideas = new ArrayList<Idee>();
	
	public User(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.birthday = null;
	}

	public User(int id, String name, String email, Date birthday) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.birthday = birthday;
	}

	public Date getBirthday() {
		return birthday;
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
