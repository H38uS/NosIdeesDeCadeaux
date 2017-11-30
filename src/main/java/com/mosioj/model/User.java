package com.mosioj.model;

import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class User {

	public final int id;
	public String email;
	public String name;
	public Date birthday;
	public boolean isInMyNetwork;
	public int nbDaysBeforeBirthday;
	public String freeComment;
	public String avatar;

	private final List<Idee> ideas = new ArrayList<Idee>();

	public User(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public User(int id, String name, String email, String avatar) {
		this(id, name, email);
		this.avatar = avatar == null ? "default.png" : avatar;
	}

	public User(int id, String name, String email, Date birthday, String avatar) {
		this(id, name, email, avatar);
		this.birthday = birthday;
	}

	public User(int id, String name, String email, Date birthday, String avatar, int nbDaysBeforeBirthday) {
		this(id, name, email, birthday, avatar);
		this.nbDaysBeforeBirthday = nbDaysBeforeBirthday;
	}

	public boolean getIsInMyNetwork() {
		return isInMyNetwork;
	}

	public Date getBirthday() {
		return birthday;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public String getAvatarSrcSmall() {
		return MessageFormat.format("small/{0}", avatar);
	}

	public String getAvatarSrcLarge() {
		return MessageFormat.format("large/{0}", avatar);
	}

	public int getNbDaysBeforeBirthday() {
		return nbDaysBeforeBirthday;
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
		return name != null && !name.trim().isEmpty() ? name : email;
	}
	
	public void addIdeas(List<Idee> ownerIdeas) {
		ideas.addAll(ownerIdeas);
	}

	public List<Idee> getIdeas() {
		return ideas;
	}

	public String getFreeComment() {
		return freeComment;
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

	@Override
	public String toString() {
		return name + " (" + email + ")";
	}

}
