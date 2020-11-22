package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.text.WordUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class User implements Comparable<User> {

    /**
     * Maximum number of days to trigger the notification birthday is closed.
     */
    public static final int NB_DAYS_BEFORE_BIRTHDAY = 5;

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

    @Expose
    public String freeComment; // utilisé dans suggestion relation en jsp - impossible de supprimer le getter pour l'instant

    // TODO : il faut changer les Timestamp/Date en Instant ou ZonedDateTime
    private Date birthday; // utilisé dans MonCompte en jsp - impossible de supprimer le getter pour l'instant

    /** Formatted instant of the creation of the user. */
    private String creationDate; // utilisé dans l'admin en jsp - impossible de supprimer le getter pour l'instant

    /** Formatted instant of the last login. */
    private String lastLogin; // utilisé dans l'admin en jsp - impossible de supprimer le getter pour l'instant

    public long nbDaysBeforeBirthday; // utilisé dans l'index en jsp - impossible de supprimer le getter pour l'instant

    public boolean hasBookedOneOfItsIdeas = false; // utilisé dans l'index en jsp - impossible de supprimer le getter pour l'instant

    public User(int id, String name, String email, Date birthday, String avatar) {
        this.id = id;
        this.name = name == null || name.trim().isEmpty() ? email : WordUtils.capitalize(name.trim());
        this.email = email;
        this.avatar = avatar == null ? "default.png" : avatar;
        this.birthday = birthday;
        this.nbDaysBeforeBirthday = getNbDayBeforeBirthday(LocalDate.now(), birthday).orElse(Long.MAX_VALUE);
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
        this.creationDate = MyDateFormatViewer.formatMine(creationDate);
        this.lastLogin = MyDateFormatViewer.formatMine(lastLogin);
    }

    /**
     * @param now      The current date for which we want to get the difference. Useful for testing.
     * @param birthday The birth date.
     * @return The number of days before this birthday.
     */
    public static Optional<Long> getNbDayBeforeBirthday(LocalDate now, Date birthday) {

        if (now == null || birthday == null) {
            return Optional.empty();
        }

        LocalDate birthDateAtCurrentYear = Instant.ofEpochMilli(birthday.getTime())
                                                  .atZone(ZoneId.of("Europe/Paris"))
                                                  .toLocalDate()
                                                  .withYear(now.getYear());
        if (birthDateAtCurrentYear.isBefore(now)) {
            // Getting the one of the next year !
            birthDateAtCurrentYear = birthDateAtCurrentYear.withYear(now.getYear() + 1);
        }

        return Optional.of(ChronoUnit.DAYS.between(now, birthDateAtCurrentYear));
    }

    /**
     * @return The birthday if set by the users.
     */
    public Optional<Date> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    /**
     * Sets the new birthday of this person. Can be null.
     *
     * @param birthday The new birthday.
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
        if (birthday != null) {
            this.nbDaysBeforeBirthday = getNbDayBeforeBirthday(LocalDate.now(), birthday).orElse(Long.MAX_VALUE);
        }
    }

    /**
     * @return The formatted birthdate.
     */
    public String getBirthdayAsString() {
        return getBirthday().map(b -> MyDateFormatViewer.formatDayWithYearHidden(b.toInstant()))
                            .orElse("- on ne sait pas...");
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return The user's avatar or the default one.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return True if the connected user has booked one of this user ideas, or is participating to a group.
     */
    public boolean getHasBookedOneOfItsIdeas() {
        return hasBookedOneOfItsIdeas;
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return the creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return the lastLogin
     */
    public String getLastLogin() {
        return lastLogin;
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return True if the user has already set up an avatar.
     */
    public boolean hasSetUpAnAvatar() {
        return !"default.png".equals(getAvatar());
    }

    /**
     * @return The small avatar picture.
     */
    public String getAvatarSrcSmall() {
        return MessageFormat.format("small/{0}", avatar);
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return The large avatar picture.
     */
    public String getAvatarSrcLarge() {
        return MessageFormat.format("large/{0}", avatar);
    }

    public long getNbDaysBeforeBirthday() {
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

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return The user's free comment.
     */
    public String getFreeComment() {
        return freeComment;
    }

    /**
     * @return The name with the email or the name with the email between parenthesis.
     */
    public String getLongNameEmail() {
        return MessageFormat.format("{0} ({1})", WordUtils.capitalize(name), email);
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return A computed name with an article "de" or "d'".
     */
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
