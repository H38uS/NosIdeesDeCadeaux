package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.text.WordUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class User implements Comparable<User> {

    /** The user's id. */
    @Expose
    public final int id;

    /** The user's email. Cannot be null or empty. */
    @Expose
    public String email;

    /** The user's name or email (if no name yet). Cannot be null or empty. */
    @Expose
    public String name;

    /** The user's profile picture. */
    @Expose
    public String avatar;

    public Date birthday;
    private Timestamp creationDate;
    private Timestamp lastLogin;

    // FIXME : faire le ménage dans les champs/getters quand passé en JSON + faire une passe sur les méthodes
    @Expose
    public String freeComment;

    public int nbDaysBeforeBirthday;
    public boolean hasBookedOneOfItsIdeas = false;
    private final List<Idee> ideas = new ArrayList<>();

    public User(int id, String name, String email, Date birthday, String avatar) {
        this.id = id;
        this.name = name == null || name.trim().isEmpty() ? email : WordUtils.capitalize(name.trim());
        this.email = email;
        this.avatar = avatar == null ? "default.png" : avatar;
        this.birthday = birthday;
    }

    public User(int id, String name, String email, Date birthday, String avatar, int nbDaysBeforeBirthday) {
        this(id, name, email, birthday, avatar);
        this.nbDaysBeforeBirthday = nbDaysBeforeBirthday; // FIXME : le calculer tout le temps, en Java
    }

    /**
     * Administration constructor.
     *
     * @param id           The user's ID.
     * @param name         The user's name.
     * @param email        The user's email.
     * @param avatar       The user's avatar.
     * @param creationDate When this user has been created.
     * @param lastLogin    When it has last logged in.
     */
    public User(int id,
                String name,
                String email,
                Date birthday,
                String avatar,
                Timestamp creationDate,
                Timestamp lastLogin) {
        this(id, name, email, birthday, avatar);
        this.creationDate = creationDate;
        this.lastLogin = lastLogin;
    }

    /**
     * @return The birthday if set by the users.
     */
    public Optional<Date> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    /**
     * @return The formatted birthdate.
     */
    public String getBirthdayAsString() {
        return getBirthday().map(b -> MyDateFormatViewer.formatDayWithYearHidden(b.getTime()))
                            .orElse("- on ne sait pas...");
    }

    public String getAvatar() {
        return avatar;
    }

    /**
     * @return True if the connected user has booked one of this user ideas, or is participating to a group.
     */
    public boolean getHasBookedOneOfItsIdeas() {
        return hasBookedOneOfItsIdeas;
    }

    /**
     * @return the creationDate
     */
    public String getCreationDate() {
        return MyDateFormatViewer.formatMine(creationDate);
    }

    /**
     * @return the lastLogin
     */
    public String getLastLogin() {
        return MyDateFormatViewer.formatMine(lastLogin);
    }

    /**
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

    /**
     * @param token The caracter to search.
     * @return True if this user's name or email contains the token.
     */
    public boolean matchNameOrEmail(String token) {
        return getName().toLowerCase().contains(token.toLowerCase()) ||
               email.toLowerCase().contains(token.toLowerCase());
    }

    /**
     * @return The id of the person.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The email of the person.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return The name of the person.
     */
    public String getName() {
        return name;
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
     * @return The name with the email or the name with the email between parenthesis.
     */
    public String getLongNameEmail() {
        return MessageFormat.format("{0} ({1})", WordUtils.capitalize(name), email);
    }

    public String getMyDName() {
        String vowel = "aeiuoyéè";
        final boolean hasVowel = vowel.indexOf(Character.toLowerCase(name.charAt(0))) == -1;
        return hasVowel ? MessageFormat.format("de {0}", getName()) : MessageFormat.format("d''{0}", getName());
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
        return id == other.id;
    }

    @Override
    public String toString() {
        return getName() + " (" + email + ")";
    }

    @Override
    public int compareTo(User other) {
        return getName().compareTo(other.getName());
    }
}
