package com.mosioj.model;

public class Comment {

	private int id;
	private String text;
	private User writtenBy;
	private int idea;

	public Comment(int id, String text, User writtenBy, int idea) {
		super();
		this.id = id;
		this.text = text;
		this.writtenBy = writtenBy;
		this.idea = idea;
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
}
