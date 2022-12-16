package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;

import javax.persistence.*;

@Entity(name = "IS_UP_TO_DATE")
public final class IsUpToDate {

    /** Internal id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    /** The idea on which the user is asking */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idee_id")
    private Idee idea;

    /** The user who is asking */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Expose
    private User askedBy;

    /**
     * @param user The user who is asking
     * @param idea The idea on which he is asking
     * @return The new created request.
     */
    public static IsUpToDate getIt(User user, Idee idea) {
        IsUpToDate request = new IsUpToDate();
        request.askedBy = user;
        request.idea = idea;
        return request;
    }
}
