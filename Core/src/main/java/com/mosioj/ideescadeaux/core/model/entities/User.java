package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

@Entity(name = "USERS")
public class User implements Comparable<User>, Serializable {

    /** Maximum number of days to trigger the notification birthday is closed. */
    public static final int NB_DAYS_BEFORE_BIRTHDAY = 5;

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    public int id;

    /** The user's hashed password. */
    @Column(length = 300)
    private String password;

    /** The user's email. Cannot be null or empty. */
    @Column(length = 100, unique = true)
    @Expose
    public String email;

    /** The user's name or email (if no name yet). Cannot be null or empty. */
    @Column(length = 100)
    @Expose
    public String name;

    @Column
    private LocalDate birthday; // utilisé dans MonCompte en jsp - impossible de supprimer le getter pour l'instant
    // FIXME faire le ménage des JSP

    /** The user's profile picture. */
    @Column(length = 200)
    @Expose
    public String avatar;

    /** Formatted instant of the last login. */
    @Column(name="last_login")
    private LocalDateTime lastLogin; // utilisé dans l'admin en jsp - impossible de supprimer le getter pour l'instant

    /** Formatted instant of the creation of the user. */
    @Column(updatable = false, name = "creation_date")
    @CreationTimestamp
    @Expose
    private LocalDateTime creationDate; // utilisé dans l'admin en jsp - impossible de supprimer le getter pour l'instant

    /** Last time this user was updated. */
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public long nbDaysBeforeBirthday; // utilisé dans l'index en jsp - impossible de supprimer le getter pour l'instant

    @OneToMany(mappedBy = "user")
    private Set<UserRole> roles;

    public User() {
        // Hibernate constructor
    }

    public User(String email, String name, String hashPwd) {
        // Constructeur pour la création de compte
        this.email = email;
        this.name = name;
        this.password = hashPwd;
    }

    public User(int id, String name, String email, Date birthday, String avatar) {
        // FIXME : supprimer quand tout est migré sur Hibernate
        this.id = id;
        this.name = name == null || name.trim().isEmpty() ? email : WordUtils.capitalize(name.trim());
        this.email = email;
        this.avatar = avatar == null ? "default.png" : avatar;
        this.birthday = birthday == null ? null : birthday.toLocalDate();
        this.nbDaysBeforeBirthday = getNbDayBeforeBirthday(LocalDate.now(), this.birthday).orElse(Long.MAX_VALUE);
    }

    @PostLoad
    private void postLoad() {
        nbDaysBeforeBirthday = getNbDayBeforeBirthday(LocalDate.now(), birthday).orElse(Long.MAX_VALUE);
        avatar = Optional.ofNullable(avatar).orElse("default.png");
        name = name == null || name.trim().isEmpty() ? email : WordUtils.capitalize(name.trim());
    }

    public boolean isAdmin() {
        return roles.contains(new UserRole(this, UserRole.RoleName.ROLE_ADMIN));
    }

    /**
     * @param token The character to search.
     * @return True if this user's name or email contains the token.
     */
    public boolean matchNameOrEmail(String token) {
        return getName().toLowerCase().contains(token.toLowerCase()) ||
               email.toLowerCase().contains(token.toLowerCase());
    }

    /**
     * @param now      The current date for which we want to get the difference. Useful for testing.
     * @param birthday The birth date.
     * @return The number of days before this birthday.
     */
    public static Optional<Long> getNbDayBeforeBirthday(LocalDate now, LocalDate birthday) {
        if (now == null || birthday == null) {
            return Optional.empty();
        }
        LocalDate birthDateAtCurrentYear = birthday.withYear(now.getYear());
        if (birthDateAtCurrentYear.isBefore(now)) {
            // Getting the one of the next year !
            birthDateAtCurrentYear = birthDateAtCurrentYear.withYear(now.getYear() + 1);
        }
        return Optional.of(ChronoUnit.DAYS.between(now, birthDateAtCurrentYear));
    }

    /**
     * Updates the last login date time to now.
     */
    public void touchLastLogin() {
        lastLogin = LocalDateTime.now();
    }

    // Setters & Getters

    /**
     * @return The birthday if set by the users.
     */
    public Optional<LocalDate> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    /**
     * Sets the new birthday of this person. Can be null.
     *
     * @param birthday The new birthday.
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        if (birthday != null) {
            this.nbDaysBeforeBirthday = getNbDayBeforeBirthday(LocalDate.now(), birthday).orElse(Long.MAX_VALUE);
        }
    }

    /**
     * @return The formatted birthdate.
     */
    public String getBirthdayAsString() {
        return getBirthday().map(MyDateFormatViewer::formatDayWithYearHidden)
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
     * @return the creationDate
     */
    public String getCreationDate() {
        return MyDateFormatViewer.formatOrElse(creationDate, StringUtils.EMPTY);
    }

    /**
     * Used in several JSP, impossible to delete.
     *
     * @return the lastLogin
     */
    public String getLastLogin() {
        return MyDateFormatViewer.formatOrElse(lastLogin, StringUtils.EMPTY);
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

    /**
     * @return The number of days before the birthday.
     */
    public long getNbDaysBeforeBirthday() {
        return nbDaysBeforeBirthday;
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

    /**
     * @return The user's hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    // Utils & interfaces

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
