package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;

import javax.persistence.*;

@Entity(name = "USER_PARAMETERS")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "parameter_name"}))
public class UserParameter {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    /** The user's id. Cannot be null or empty. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "parameter_name", length = 100)
    public String parameterName;

    @Column(name = "parameter_value", length = 500)
    public String parameterValue;

    public UserParameter() {
        // Hibernate one
    }

    public UserParameter(User user, String parameterName, String parameterValue) {
        this.user = user;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public int getId() {
        return id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public String getParameterDescription() {
        return NType.valueOf(parameterName).getDescription();
    }

}
