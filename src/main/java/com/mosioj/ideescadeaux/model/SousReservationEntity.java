package com.mosioj.ideescadeaux.model;

import java.sql.Timestamp;

public class SousReservationEntity {

	public final int id;
	public int ideeId;
	public User user;
	public String comment;
	private final Timestamp bookedOn;

	public SousReservationEntity(int id, int ideeId, User user, String comment, Timestamp bookedOn) {
		this.id = id;
		this.ideeId = ideeId;
		this.user = user;
		this.comment = comment;
		this.bookedOn = bookedOn;
	}
	
	public Timestamp getBookedOn() {
		return bookedOn;
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
