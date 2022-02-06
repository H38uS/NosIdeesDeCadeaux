package com.mosioj.ideescadeaux.core.model.entities;

import javax.persistence.*;

@Entity(name = "USER_ROLES")
public class UserRole {

    public enum RoleName {
        ROLE_USER, ROLE_ADMIN, ROLE_MANAGER
    }

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    /** The user's email. Cannot be null or empty. */
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    public User user;

    /** The user's email. Cannot be null or empty. */
    @Column(length = 15)
    public RoleName role;

    public UserRole() {
        // Hibernate one
    }

    public UserRole(User user, RoleName role) {
        this.user = user;
        this.role = role;
    }
}
