package com.mosioj.model;

import java.sql.Timestamp;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.MyDateFormatViewer;

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
		return new MyDateFormatViewer(IdeesCadeauxServlet.DATETIME_DISPLAY_FORMAT).format(time);
	}
}
