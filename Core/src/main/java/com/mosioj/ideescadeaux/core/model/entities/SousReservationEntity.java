package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;

public class SousReservationEntity {

    public final int id;
    public int ideeId;
    public User user;
    public String comment;
    private final Timestamp bookedOn; // TODO : il faut changer les Timestamp/Date en Instant ou ZonedDateTime

    public SousReservationEntity(int id, int ideeId, User user, String comment, Timestamp bookedOn) {
        this.id = id;
        this.ideeId = ideeId;
        this.user = user;
        this.comment = comment;
        this.bookedOn = bookedOn;
    }

    public String getBookedOn() {
        return MyDateFormatViewer.formatMine(bookedOn);
    }

    public int getId() {
        return id;
    }

    public int getIdeeId() {
        return ideeId;
    }

    public User getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

}
