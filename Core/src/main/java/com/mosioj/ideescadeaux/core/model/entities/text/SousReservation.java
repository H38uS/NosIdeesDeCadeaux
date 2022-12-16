package com.mosioj.ideescadeaux.core.model.entities.text;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "SOUS_RESERVATION")
public class SousReservation extends EntityWithText {

    /** Internal id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;


    /** The idea on which this question is written */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idee_id")
    private Idee idea;

    /** The user that have written this comment */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Expose
    private User user;

    /** When this question was first created. */
    @Column(updatable = false, name = "date_reservation")
    @CreationTimestamp
    @Expose
    private LocalDateTime creationDate;

    @Transient
    @Expose
    private String lastEditedOn;

    @PostLoad
    public void postLoad() {
        this.lastEditedOn = MyDateFormatViewer.formatMine(creationDate);
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    /**
     * @param user The user who posted a new question/answer.
     * @param idea The idea on which this question is posted.
     * @param text The message text.
     * @return The new created comment.
     */
    public static SousReservation getIt(User user, Idee idea, String text) {
        SousReservation booking = new SousReservation();
        booking.user = user;
        booking.idea = idea;
        booking.setText(text);
        return booking;
    }
}
