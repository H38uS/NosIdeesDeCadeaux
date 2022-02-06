package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "USER_RELATIONS_SUGGESTION")
public class RelationSuggestion {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    public int id;

    @ManyToOne
    @JoinColumn(name = "suggested_by")
    @Expose
    public User suggestedBy;

    @ManyToOne
    @JoinColumn(name = "suggested_to")
    @Expose
    public User suggestedTo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Expose
    public User suggestion;

    /** Last time this relation was updated. */
    @Column
    @UpdateTimestamp
    private LocalDateTime suggestedDate;

    public RelationSuggestion() {
        // For Hibernate
    }

    public RelationSuggestion(User userMakingSuggestion, User toUser, User suggestedUser) {
        this.suggestedBy = userMakingSuggestion;
        this.suggestedTo = toUser;
        this.suggestion = suggestedUser;
    }
}
