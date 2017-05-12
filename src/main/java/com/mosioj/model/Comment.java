package com.mosioj.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.mosioj.servlets.IdeesCadeauxServlet;

public class Comment {

	private int id;
	private String text;
	private User writtenBy;
	private int idea;
	private Timestamp time;

	public Comment(int id, String text, User writtenBy, int idea, Timestamp time) {
		super();
		this.id = id;
		this.text = text;
		this.writtenBy = writtenBy;
		this.idea = idea;
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public User getWrittenBy() {
		return writtenBy;
	}

	public int getIdea() {
		return idea;
	}

	public String getTime() {
		return new SimpleDateFormat(IdeesCadeauxServlet.DATETIME_DISPLAY_FORMAT).format(time);
	}
}
