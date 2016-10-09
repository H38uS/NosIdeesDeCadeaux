package com.mosioj.model;

public class Idee {

	private final int id;
	private final String text;
	private final String type;

	public Idee(int pId, String pText, String pType) {
		id = pId;
		text = pText.replaceAll("&lt;br/&gt;", "\n");
		type = pType;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getType() {
		return type;
	}
}
