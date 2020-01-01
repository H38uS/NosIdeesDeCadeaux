package com.mosioj.ideescadeaux.core.model.entities;

import java.sql.Date;

public class RelationRequest {

	private final User sent_by;
	private final User sent_to;
	private final Date request_date;

	public RelationRequest(User sent_by, User sent_to, Date request_date) {
		this.sent_by = sent_by;
		this.sent_to = sent_to;
		this.request_date = request_date;
	}

	public User getSent_by() {
		return sent_by;
	}

	public User getSent_to() {
		return sent_to;
	}

	public Date getRequest_date() {
		return request_date;
	}

}
