package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "USER_RELATION_REQUESTS")
public class RelationRequest {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "sent_by_user")
    @Expose
    public User sent_by;

    @ManyToOne
    @JoinColumn(name = "sent_to_user")
    @Expose
    public User sent_to;

    @Column(name = "request_date")
    @UpdateTimestamp
    @Expose
    public LocalDate request_date;

    public RelationRequest() {
        // For hibernate
    }

    public RelationRequest(User sendBy, User sentTo) {
        this.sent_by = sendBy;
        this.sent_to = sentTo;
    }
}
