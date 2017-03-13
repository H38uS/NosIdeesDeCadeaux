package com.mosioj.model;

public class Notification {

	public int id;
	public int owner;
	public String type;
	public String text;
	
	public Notification(int id, int owner, String type, String text) {
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public int getOwner() {
		return owner;
	}

	public String getType() {
		return type;
	}

	public String getText() {
		return text;
	}

}
