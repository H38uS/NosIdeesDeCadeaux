package com.mosioj.ideescadeaux.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.utils.date.MyDateFormatViewer;

public class User {

	@Expose
	public final int id;
	
	@Expose
	public String email;
	
	@Expose
	public String name;

	@Expose
	public String avatar;

	public Date birthday;
	public boolean isInMyNetwork;
	public int nbDaysBeforeBirthday;
	public String freeComment;
	private Timestamp creationDate;
	private Timestamp lastLogin;
	public boolean hasBookedOneOfItsIdeas = false;

	private final List<Idee> ideas = new ArrayList<Idee>();

	public User(int id, String name, String email, String avatar) {
		this.id = id;
		this.name = name == null ? null : name.trim();
		this.email = email;
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

	/**
	 * Administration constructor.
	 * 
	 * @param id
	 * @param name
	 * @param email
	 * @param avatar
	 * @param creationDate
	 * @param lastLogin
	 */
	public User(int id, String name, String email, String avatar, Timestamp creationDate, Timestamp lastLogin) {
		this(id, name, email, avatar);
		this.creationDate = creationDate;
		this.lastLogin = lastLogin;
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

	/**
	 * 
	 * @return True if the connected user has booked one of this user ideas, or is participating to a group.
	 */
	public boolean getHasBookedOneOfItsIdeas() {
		return hasBookedOneOfItsIdeas;
	}

	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		if (creationDate == null) {
			return "";
		}
		return new MyDateFormatViewer(MyDateFormatViewer.DATETIME_DISPLAY_FORMAT).format(creationDate);
	}

	/**
	 * @return the lastLogin
	 */
	public String getLastLogin() {
		if (lastLogin == null) {
			return "";
		}
		return new MyDateFormatViewer(MyDateFormatViewer.DATETIME_DISPLAY_FORMAT).format(lastLogin);
	}

	/**
	 * 
	 * @return True if the user has already set up an avatar.
	 */
	public boolean hasSetUpAnAvatar() {
		return !"default.png".equals(getAvatar());
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

	public boolean matchNameOrEmail(String token) {
		return getName().toLowerCase().contains(token.toLowerCase()) || email.toLowerCase().contains(token.toLowerCase());
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
		return name != null && !name.isEmpty() ? WordUtils.capitalize(name) : email;
	}

	public void setIdeas(List<Idee> ownerIdeas) {
		ideas.clear();
		ideas.addAll(ownerIdeas);
	}

	public List<Idee> getIdeas() {
		return ideas;
	}

	public String getFreeComment() {
		return freeComment;
	}

	/**
	 * 
	 * @return The name with the email or the name with the email between parenthesis.
	 */
	public String getLongNameEmail() {
		return name != null && !name.isEmpty() ? MessageFormat.format("{0} ({1})", WordUtils.capitalize(name), email) : email;
	}

	public String getMyDName() {
		if (name == null || name.isEmpty()) {
			return MessageFormat.format("de {0}", email);
		}
		String vowel = "aeiuoyéè";
		return vowel.indexOf(Character.toLowerCase(name.charAt(0))) == -1 ? MessageFormat.format("de {0}", getName())
				: MessageFormat.format("d''{0}", getName());
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
		return getName() + " (" + email + ")";
	}

}
